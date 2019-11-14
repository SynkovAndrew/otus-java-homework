package com.otus.java.coursework;

import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.serialization.Message;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ClientApplication {
    public static void main(String[] args) {
        final var serializer = new Serializer();
        SocketChannelUtils.openSocketChannel("localhost", 4455).ifPresent(socketChannel -> {
            System.out.println("Sending message to server...");

            final CreateUserRequestDTO request = CreateUserRequestDTO.builder().age(11).name("Pavel").build();
            final var message = new Message<>(request);

            serializer.map(message).ifPresent(json -> {
                final var buffer = ByteBuffer.wrap((json + '\n').getBytes());
                SocketChannelUtils.write(socketChannel, buffer);
                buffer.clear();
                SocketChannelUtils.read(socketChannel, buffer);
                String response = new String(buffer.array()).trim();
                System.out.println("response=" + response);
                buffer.clear();
            });

            SocketChannelUtils.close(socketChannel);
        });
    }
}
