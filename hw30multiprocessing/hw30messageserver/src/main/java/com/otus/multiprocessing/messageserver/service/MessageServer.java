package com.otus.multiprocessing.messageserver.service;

import com.otus.multiprocessing.messageserver.socket.MessageProcessor;
import com.otus.multiprocessing.messageserver.socket.MessageProcessorType;
import com.otus.multiprocessing.messageserver.socket.SocketMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static com.otus.multiprocessing.messageserver.socket.MessageProcessorType.DATABASE;
import static com.otus.multiprocessing.messageserver.socket.MessageProcessorType.FRONTEND;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Service
@Slf4j
public class MessageServer {
    private static final int THREAD_COUNT = 4;
    private final ExecutorService executorService;
    private final Map<MessageProcessorType, MessageProcessor> messageProcessors;
    @Value("${socket.server.frontend.port}")
    private int frontendPort;
    @Value("${socket.server.database.port}")
    private int databasePort;

    public MessageServer() {
        this.messageProcessors = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(THREAD_COUNT);
    }

    @PostConstruct
    void init() {
        executorService.execute(() -> processSocketConnection(frontendPort, FRONTEND));
        executorService.execute(() -> processSocketConnection(databasePort, DATABASE));
        executorService.execute(() -> processMessage(DATABASE));
        executorService.execute(() -> processMessage(FRONTEND));
    }

    @PreDestroy
    void destroy() {
        messageProcessors.forEach((key, value) -> {
            try {
                value.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        executorService.shutdown();
    }

    private void processSocketConnection(final int port, final MessageProcessorType type) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (!executorService.isShutdown()) {
                final var socket = serverSocket.accept();
                final var messageProcessor = new SocketMessageProcessor(socket, type);
                messageProcessors.put(type, messageProcessor);
                log.info("Client {}'s been connected to message server", type);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processMessage(final MessageProcessorType type) {
        while (!executorService.isShutdown()) {
            ofNullable(messageProcessors.get(type)).ifPresent(processor ->
                    ofNullable(processor.pool()).ifPresent(message -> {
                        log.info("Processing the message {} from {}", message, type);
                        if (type.equals(DATABASE)) {
                            messageProcessors.get(FRONTEND).send(message);
                        } else {
                            messageProcessors.get(DATABASE).send(message);
                        }
                    })
            );
        }
    }
}
