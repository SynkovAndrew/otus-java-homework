package com.otus.multiprocessing.messageserver.socket;

import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static messageV2.MessageSocketService.receiveMessage;
import static messageV2.MessageSocketService.sendMessage;

@Slf4j
public class SocketMessageProcessor implements MessageProcessor {
    private static final int THREAD_COUNT = 2;

    private final ExecutorService executorService;
    private final Socket socket;

    private final BlockingQueue<Message<? extends ParentDTO>> outputQueue;
    private final BlockingQueue<Message<? extends ParentDTO>> inputQueue;

    public SocketMessageProcessor(final Socket socket) {
        this.socket = socket;
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.inputQueue = new ArrayBlockingQueue<>(10);
        this.outputQueue = new ArrayBlockingQueue<>(10);
        this.executorService.submit(() -> receiveMessage(socket, inputQueue));
        this.executorService.submit(() -> sendMessage(socket, outputQueue));
    }

    @Override
    public Message<? extends ParentDTO> pool() {
        return inputQueue.poll();
    }

    @Override
    public void send(final Message<? extends ParentDTO> message) {
        outputQueue.add(message);
    }

    @Override
    public Message<? extends ParentDTO> take() throws InterruptedException {
        return inputQueue.take();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }
}
