package com.otus.java.coursework.client;

import com.otus.java.coursework.dto.BaseDTO;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Message;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class Client implements AutoCloseable {
    private final Serializer serializer;
    private final SocketChannel socketChannel;

    public Client(final String host, final int port) throws FailedToCreateClientException {
        this.socketChannel = SocketChannelUtils.openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.serializer = new Serializer();
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final BaseDTO dto) {
        log.info("Sending message {} to server {}...", dto, SocketChannelUtils.getRemoteAddress(socketChannel).get());
        final var message = new Message<>(dto);
        serializer.map(message).ifPresent(json -> {
            final var buffer = ByteBuffer.wrap((json + '\n').getBytes());
            SocketChannelUtils.write(socketChannel, buffer);
            buffer.clear();
            SocketChannelUtils.read(socketChannel, buffer);
            final String response = new String(buffer.array()).trim();
            log.info("Server {} has responded with {} ", SocketChannelUtils.getRemoteAddress(socketChannel).get(), response);
            buffer.clear();
        });
    }
}
