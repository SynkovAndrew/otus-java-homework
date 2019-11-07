package com.otus.java.projectwork.nioserver.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class Server {
    private final static byte END_OF_MESSAGE = '\n';
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final Map<SocketChannel, ByteBuffer> socketChannels;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;

    public Server() throws IOException {
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.socketChannels = new ConcurrentHashMap<>();
    }

    @PreDestroy
    public void destroy() throws IOException {
        selector.close();
        serverSocketChannel.close();
    }

    @PostConstruct
    public void init() throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("Server's been started at {}:{}", host, port);
        run();
    }

    private void read(final SelectionKey key) throws IOException {
        final SocketChannel client = (SocketChannel) key.channel();
        final ByteBuffer buffer = socketChannels.get(client);
        final int readBytes = client.read(buffer);

        // in case of result is equal to -1 the connection's closed from client side
        if (readBytes == -1) {
            log.info("{} connection's been closed", client.getRemoteAddress());
            socketChannels.remove(client);
            client.close();
        }

        if (readBytes > 0 && END_OF_MESSAGE == buffer.get(buffer.position() - 1)) {
            client.register(selector, SelectionKey.OP_WRITE);
            buffer.flip();
            final String message = new String(buffer.array(), buffer.position(), buffer.limit());
            log.info("Message {} from {} 's been received", message, client.getRemoteAddress());
        }
    }

    private void accept() throws IOException {
        final var client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        socketChannels.put(client, ByteBuffer.allocate(1024));
        log.info("Client {}'s been connected to server", client.getRemoteAddress());
    }

    private void write(final SelectionKey key) throws IOException {
        final SocketChannel client = (SocketChannel) key.channel();
        final ByteBuffer buffer = socketChannels.get(client);

        final String response = "Response from server";
        buffer.clear();
        buffer.put(ByteBuffer.wrap(response.getBytes()));
        buffer.flip();

        final int writtenBytes = client.write(buffer);
        log.info("{} bytes've been written to {}", writtenBytes, client.getRemoteAddress());
        if (!buffer.hasRemaining()) {
            buffer.compact();
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public void run() throws IOException {
        while (true) {
            selector.select(); // Blocking call. The current thread will be blocked till a client connect to server.
            final Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                // a client's been connected to server
                if (key.isAcceptable()) {
                    accept();
                }

                if (key.isReadable()) {
                    read(key);
                }

                if (key.isWritable()) {
                    write(key);
                }

                iter.remove();
            }
        }
    }
}
