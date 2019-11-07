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
    private final static String END_OF_MESSAGE = "EOM";
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
    }

    private void read(final SelectionKey key) throws IOException {
        final SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        if (new String(buffer.array()).trim().equals(END_OF_MESSAGE)) {
            client.close();
            log.info("Message's been received");
        }
        buffer.flip();
        client.write(buffer);
        buffer.clear();
    }

    private void register() throws IOException {
        final var client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        socketChannels.put(client, ByteBuffer.allocate(1024));
        log.info("Client {}'s been connected to server", client.getRemoteAddress());
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
                    register();
                }

                if (key.isReadable()) {
                    read(key);
                }
                iter.remove();
            }
        }
    }
}
