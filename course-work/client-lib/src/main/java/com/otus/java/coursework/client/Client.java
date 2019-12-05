package com.otus.java.coursework.client;

import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;

import static com.otus.java.coursework.utils.SocketChannelUtils.*;
import static java.nio.ByteBuffer.wrap;

@Slf4j
public class Client implements AutoCloseable {
    private final SocketChannel socketChannel;
    private final Serializer serializer;

    public Client(final String host,
                  final int port, final
                  Serializer serializer) throws FailedToCreateClientException {
        this.socketChannel = openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.serializer = serializer;
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final Object object) {
        serializer.writeObject(object).ifPresent(bytes -> {
            final var buffer = wrap(bytes);
            write(socketChannel, buffer);
            buffer.clear();
            final int readBytes = read(socketChannel, buffer);
            if (readBytes > 0) {
                serializer.readObject(buffer.array(), Object.class)
                        .ifPresent(response -> log.info("Server responded with: {}", response));
            }
        });
    }
}
