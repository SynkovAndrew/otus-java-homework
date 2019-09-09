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
    private final MessageProcessorType type;
    private final BlockingQueue<Message<? extends ParentDTO>> outputQueue;
    private final BlockingQueue<Message<? extends ParentDTO>> inputQueue;

    public SocketMessageProcessor(final Socket socket, final MessageProcessorType type) {
        this.socket = socket;
        this.type = type;
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.inputQueue = new ArrayBlockingQueue<>(10);
        this.outputQueue = new ArrayBlockingQueue<>(10);
        this.executorService.execute(() -> receiveMessage(socket, inputQueue));
        this.executorService.execute(() -> sendMessage(socket, outputQueue));
        log.info("Socket message processor {}'s been started", type);
    }

    @Override
    public Message<? extends ParentDTO> pool() {
        return inputQueue.poll();
    }

    @Override
    public void send(final Message<? extends ParentDTO> message) {
        log.info("Message's been sent: {}", message);
        outputQueue.add(message);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }
}
