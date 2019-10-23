package com.otus.multiprocessing.messageserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket.MessageProcessor;
import socket.MessageProcessorType;
import socket.SocketMessageProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import static com.otus.multiprocessing.messageserver.utils.ProcessRunnerUtils.DATABASE_CLIENT_PORTS;
import static com.otus.multiprocessing.messageserver.utils.ProcessRunnerUtils.FRONTEND_CLIENT_PORTS;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static socket.MessageProcessorType.DATABASE;
import static socket.MessageProcessorType.FRONTEND;

@Service
@Slf4j
public class MessageServer {
    private static final int THREAD_COUNT = 4;
    private final ExecutorService executorService;
    private final List<Map<MessageProcessorType, MessageProcessor>> messageProcessors;

    public MessageServer() {
        this.messageProcessors = new CopyOnWriteArrayList<>();
        this.executorService = newFixedThreadPool(THREAD_COUNT);
    }

    @PostConstruct
    void init() {
        FRONTEND_CLIENT_PORTS
                .forEach(port -> {
                    executorService.execute(() -> processSocketConnection(FRONTEND_CLIENT_PORTS.indexOf(port), port, FRONTEND));
                    executorService.execute(() -> processMessage(FRONTEND_CLIENT_PORTS.indexOf(port), FRONTEND));
                });
        DATABASE_CLIENT_PORTS
                .forEach(port -> {
                    executorService.execute(() -> processSocketConnection(DATABASE_CLIENT_PORTS.indexOf(port), port, DATABASE));
                    executorService.execute(() -> processMessage(DATABASE_CLIENT_PORTS.indexOf(port), DATABASE));
                });
    }

    @PreDestroy
    void destroy() {
        messageProcessors.stream().flatMap(mp -> mp.values().stream()).forEach((p) -> {
            try {
                p.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        executorService.shutdown();
    }

    private void processSocketConnection(final int number, final int port, final MessageProcessorType type) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (!executorService.isShutdown()) {
                final var socket = serverSocket.accept();
                final var messageProcessor = new SocketMessageProcessor(socket, type);
                messageProcessors.get(number).put(type, messageProcessor);
                log.info("Client {}'s been connected to message server", type);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processMessage(final int number, final MessageProcessorType type) {
        while (!executorService.isShutdown()) {
            messageProcessors.forEach(mp -> ofNullable(mp.get(type))
                    .ifPresent(p -> ofNullable(p.pool()).ifPresent(message -> {
                                log.info("Processing the message {} from {}", message, type);
                                if (type.equals(DATABASE)) {
                                    mp.get(FRONTEND).put(message);
                                } else {
                                    mp.get(DATABASE).put(message);
                                }
                            })
                    ));

        }
    }
}
