package com.otus.java.coursework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Optional;

import static com.otus.java.coursework.utils.ByteArrayUtils.flatMap;
import static java.util.Arrays.copyOf;
import static java.util.Optional.*;
import static org.assertj.core.util.Lists.newArrayList;

@Slf4j
public class SocketChannelUtils {
    public static Optional<SocketChannel> accept(final Selector selector,
                                                 final ServerSocketChannel serverSocketChannel) {
        try {
            final var client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            return ofNullable(client);
        } catch (IOException e) {
            log.info("Failed to accept connection from server socket channel", e);
            return empty();
        }
    }

    public static void close(final SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            log.info("Failed to close socket channel", e);
        }
    }

    public static void destroy(final Selector selector,
                               final ServerSocketChannel serverSocketChannel) {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            log.info("Failed to destroy", e);
        }
    }

    public static Optional<SocketAddress> getRemoteAddress(final SocketChannel socketChannel) {
        try {
            return ofNullable(socketChannel.getRemoteAddress());
        } catch (IOException e) {
            log.info("Failed to get remote address from socket channel", e);
            return empty();
        }
    }

    public static Optional<SocketChannel> openSocketChannel(final String host,
                                                            final int port) {
        try {
            return ofNullable(SocketChannel.open(new InetSocketAddress(host, port)));
        } catch (IOException e) {
            log.info("Failed to open socket channel", e);
            return empty();
        }
    }

    public static int read(final SocketChannel socketChannel,
                           final ByteBuffer buffer) {
        try {
            return socketChannel.read(buffer);
        } catch (IOException e) {
            log.info("Failed to write to socket channel", e);
            return 0;
        }
    }

    public static void register(final Selector selector,
                                final SocketChannel socketChannel,
                                final int selectionKey) {
        try {
            socketChannel.register(selector, selectionKey);
        } catch (IOException e) {
            log.info("Failed to register socket channel to selector", e);
        }
    }

    public static int select(final Selector selector) {
        try {
            return selector.select();
        } catch (IOException e) {
            log.info("Failed to select", e);
            return 0;
        }
    }

    public static int write(final SocketChannel socketChannel,
                            final ByteBuffer buffer) {
        try {
            return socketChannel.write(buffer);
        } catch (IOException e) {
            log.info("Failed to write to socket channel", e);
            return 0;
        }
    }

    public static Optional<byte[]> readStepByStep(final SocketChannel socketChannel,
                                                  final ByteBuffer buffer) {
        final List<byte[]> byteArrays = newArrayList();
        int readBytes = read(socketChannel, buffer);
        while (readBytes > 0) {
            buffer.flip();
            log.info("{} bytes've been read from {}", readBytes, getRemoteAddress(socketChannel).get());
            final byte[] receivedBytes = buffer.array();
            byteArrays.add(copyOf(receivedBytes, receivedBytes.length));
            buffer.flip();
            buffer.clear();
            readBytes = read(socketChannel, buffer);
        }
        if (readBytes == -1) {
            log.info("{} connection's been closed", getRemoteAddress(socketChannel).get());
            close(socketChannel);
            return empty();
        }
        return of(flatMap(byteArrays));
    }
}
