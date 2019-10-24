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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
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
    private final ProcessRunnerService processRunnerService;
    private final List<Process> processes;

    public MessageServer(final ProcessRunnerService processRunnerService) {
        this.messageProcessors = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.processRunnerService = processRunnerService;
        this.processes = newArrayList();
    }

    @PreDestroy
    void destroy() {
        processes.forEach(Process::destroy);
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
        Stream.of(MessageProcessorType.values()).forEach(type -> executorService.execute(() -> processMessages(type)));

        processes.addAll(processRunnerService.prepareProcesses().stream().map(pb -> {
            try {
                return pb.start();
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()));

/*        final ProcessBuilder processBuilder1 = processRunnerService.getDatabaseClientProcessBuilder(
                processRunnerService.DATABASE_SOCKET_PORTS.get(0)
        );
        final ProcessBuilder processBuilder2 = processRunnerService.getFrontendClientProcessBuilder(
                processRunnerService.FRONTEND_SOCKET_PORTS.get(0),
                processRunnerService.FRONTEND_WEB_SERVER_PORTS.get(0)
        );
        final Process process1 = processBuilder1.start();
        processes.add(process1);
        final Process process2 = processBuilder2.start();
        processes.add(process2);


        executorService.execute(() -> {
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process1.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(">>>>> 1111 " + line);
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        executorService.execute(() -> {
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(">>>>> 2222 " + line);
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });*/

    }

    private void processMessages(final MessageProcessorType type) {
        while (!executorService.isShutdown()) {
            messageProcessors.forEach((number, mp) -> ofNullable(mp.get(type))
                    .ifPresent(p -> ofNullable(p.pool()).ifPresent(message -> {
                                log.info("Processing the message {} from {}-{}", message, type, number);
                                if (type.equals(DATABASE)) {
                                    mp.get(FRONTEND).put(message);
                                } else {
                                    mp.get(DATABASE).put(message);
                                }
                            })
                    ));
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
                    final Map<MessageProcessorType, MessageProcessor> map = newHashMap();
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
