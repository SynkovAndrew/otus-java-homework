package com.otus.multiprocessing.messageserver.service;

import com.otus.multiprocessing.messageserver.socket.MessageProcessor;
import com.otus.multiprocessing.messageserver.socket.SocketMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Service
@Slf4j
public class MessageServer {
    private final ExecutorService executorService;
    private final List<MessageProcessor> messageProcessors;
    @Value("${socket.server.port}")
    private int port;

    public MessageServer() {
        this.messageProcessors = new CopyOnWriteArrayList<>();
        this.executorService = newSingleThreadExecutor();
    }

    @PostConstruct
    void init() throws IOException {
        executorService.submit(this::mirror);
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (!executorService.isShutdown()) {
                final var socket = serverSocket.accept();
                final var messageProcessor = new SocketMessageProcessor(socket);
                messageProcessors.add(messageProcessor);
            }
        }
    }

    @PreDestroy
    void destroy() {
        executorService.shutdown();
    }

    private void mirror() {
        while (true) {
            for (final MessageProcessor processor : messageProcessors) {
                ofNullable(processor.pool())
                        .ifPresent(message -> {
                            log.info("Mirroring the message: {}", message);
                            processor.send(message);
                        });
            }
        }
    }
}
