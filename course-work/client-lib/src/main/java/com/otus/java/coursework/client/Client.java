package com.otus.java.coursework.client;

import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class Client implements AutoCloseable {
    private final SocketChannel socketChannel;
    private final Serializer serializer;

    public Client(final String host,
                  final int port, final
                  Serializer serializer) throws FailedToCreateClientException {
        this.socketChannel = SocketChannelUtils.openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.serializer = serializer;
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final Object object) {
        serializer.writeObject(object).ifPresent(bytes -> {
            final var buffer = ByteBuffer.wrap(bytes);
            SocketChannelUtils.write(socketChannel, buffer);
            buffer.clear();
        });
    }
}
