package com.otus.java.coursework.client;

import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.service.ByteProcessorService;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;

import static com.otus.java.coursework.utils.ByteArrayUtils.BYTE_ARRAY_DELIMITER;
import static com.otus.java.coursework.utils.ByteArrayUtils.fill;
import static com.otus.java.coursework.utils.SocketChannelUtils.*;
import static java.nio.ByteBuffer.wrap;

@Slf4j
public class Client implements AutoCloseable {
    private final ByteProcessorService byteProcessorService;
    private final Serializer serializer;
    private final SocketChannel socketChannel;

    public Client(final String host,
                  final int port,
                  final Serializer serializer,
                  final ByteProcessorService byteProcessorService) throws FailedToCreateClientException {
        this.socketChannel = openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.serializer = serializer;
        this.byteProcessorService = byteProcessorService;
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final Object object) {
        serializer.writeObject(object).ifPresent(bytes -> {
            final byte[] bytesWithDelimiter = new byte[bytes.length + BYTE_ARRAY_DELIMITER.length];
            fill(bytesWithDelimiter, bytes, BYTE_ARRAY_DELIMITER);
            final var buffer = wrap(bytesWithDelimiter);
            write(socketChannel, buffer);
            buffer.clear();
            final int readByteCount = read(socketChannel, buffer);
            if (readByteCount > 0) {
                final var clientId = socketChannel.hashCode();
                byteProcessorService.initializeChunk(clientId);
                final var receivedBytes = buffer.array();
                byteProcessorService.getCompleteByteSets(clientId, receivedBytes)
                        .forEach(completeByteSet -> serializer.readObject(completeByteSet, Object.class)
                                .ifPresent(response -> log.info("Server responded with: {}", response)));
            }
        });
    }
}
