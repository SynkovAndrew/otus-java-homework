package com.otus.multiprocessing.messageserver.service;

import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Service
@Slf4j
public class MessageServer {
    private final ArrayBlockingQueue<Message<? extends ParentDTO>> queue;
    private final ExecutorService readerService;
    private final ExecutorService writerService;


    @Value("${socket.server.host}")
    private String host;
    @Value("${socket.server.port}")
    private int port;

    public MessageServer() {
        this.queue = new ArrayBlockingQueue<>(10);
        this.readerService = newSingleThreadExecutor();
        this.writerService = newSingleThreadExecutor();

    }

    @PostConstruct
    void init() throws IOException {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
/*            while (!executorService.isShutdown()) {
                Socket socket = serverSocket.accept();

            }*/
        }
    }
}
