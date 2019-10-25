package com.otus.multiprocessing.messageserver.service;

import com.otus.multiprocessing.messageserver.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket.MessageProcessor;
import socket.MessageProcessorType;
import socket.SocketMessageProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static socket.MessageProcessorType.DATABASE;
import static socket.MessageProcessorType.FRONTEND;

@Service
@Slf4j
public class MessageServer {
    private static final int THREAD_COUNT = 20;
    private final ExecutorService executorService;
    private final Map<Integer, Map<MessageProcessorType, MessageProcessor>> messageProcessors;
    private final AtomicInteger number = new AtomicInteger(0);
    private final ProcessRunnerService processRunnerService;
    private final Map<Integer, Process> processes;

    public MessageServer(final ProcessRunnerService processRunnerService) {
        this.messageProcessors = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.processRunnerService = processRunnerService;
        this.processes = newHashMap();
    }

    @PreDestroy
    void destroy() {
        processes.values().forEach(Process::destroy);
        messageProcessors.values().stream().flatMap(mp -> mp.values().stream()).forEach(p -> {
            try {
                p.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        executorService.shutdown();
    }

    @PostConstruct
    void init() {
        processRunnerService.FRONTEND_SOCKET_PORTS
                .forEach(port -> executorService.execute(() -> processSocketConnection(processRunnerService.FRONTEND_SOCKET_PORTS.indexOf(port), port, FRONTEND)));
        processRunnerService.DATABASE_SOCKET_PORTS
                .forEach(port -> executorService.execute(() -> processSocketConnection(processRunnerService.DATABASE_SOCKET_PORTS.indexOf(port), port, DATABASE)));
        executorService.execute(() -> processMessages());
        executorService.execute(() -> processMessages());
        executorService.execute(() -> processMessages());
        executorService.execute(() -> processMessages());
        executorService.execute(() -> processMessages());
    }

    private void processMessages() {
        while (!executorService.isShutdown()) {
            messageProcessors.forEach(
                    (number, messageProcessorMap) -> messageProcessorMap.values().forEach(
                            mp -> ofNullable(mp.pool())
                                    .ifPresent(message -> {
                                        if (FRONTEND.equals(mp.getType())) {
                                            messageProcessors.values().stream()
                                                    .flatMap(entry -> entry.values().stream())
                                                    .filter(p -> DATABASE.equals(p.getType()))
                                                    .findAny()
                                                    .ifPresent(p -> p.put(message));
                                        } else {
                                            messageProcessors.values().stream()
                                                    .flatMap(entry -> entry.values().stream())
                                                    .filter(p -> Objects.equals(p.getInstanceId(), message.getInstanceId()))
                                                    .findFirst()
                                                    .ifPresent(p -> p.put(message));
                                        }
                                    })
                    )
            );
        }
    }

    private void processSocketConnection(final int number, final int port, final MessageProcessorType type) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (!executorService.isShutdown()) {
                final var socket = serverSocket.accept();
                final var messageProcessor = new SocketMessageProcessor(socket, type);
                if (messageProcessors.containsKey(number)) {
                    messageProcessors.get(number).put(type, messageProcessor);
                } else {
                    final Map<MessageProcessorType, MessageProcessor> map = new ConcurrentHashMap();
                    map.put(type, messageProcessor);
                    messageProcessors.put(number, map);
                }
                log.info("Client {}-{}'s been connected to message server", type, number);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
