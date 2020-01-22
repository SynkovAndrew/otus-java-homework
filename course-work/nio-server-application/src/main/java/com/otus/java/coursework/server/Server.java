package com.otus.java.coursework.server;

import com.otus.java.coursework.executor.ServerRequestExecutor;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import static com.otus.java.coursework.utils.SocketChannelUtils.*;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static java.nio.channels.SelectionKey.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Slf4j
@Component
public class Server {
    private final static int INITIAL_BYTE_BUFFER_SIZE = 16;
    private final ConcurrentMap<Integer, ByteBuffer> byteBuffers;
    private final ServerRequestExecutor executor;
    private final Serializer serializer;
    private final ExecutorService serverRunner;
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;

    public Server(final ServerRequestExecutor executor,
                  final Serializer serializer) throws IOException {
        this.byteBuffers = new ConcurrentHashMap<>();
        this.serverRunner = newSingleThreadExecutor();
        this.executor = executor;
        this.serializer = serializer;
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, OP_ACCEPT);
    }

    @PostConstruct
    public void init() throws IOException {
        this.serverSocketChannel.bind(new InetSocketAddress(host, port));
        this.serverRunner.execute(this::run);
        log.info("Server's been started at {}:{}", host, port);
    }

    private void handleAccept() {
        accept(selector, serverSocketChannel).ifPresent(client -> {
            final var clientId = client.hashCode();
            byteBuffers.put(clientId, allocate(INITIAL_BYTE_BUFFER_SIZE));
            log.info("Client {}'s been connected to server", getRemoteAddress(client));
        });
    }

    @PreDestroy
    public void destroy() {
        SocketChannelUtils.destroy(selector, serverSocketChannel);
    }

    private void handleRead(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final int clientId = client.hashCode();
        final ByteBuffer buffer = byteBuffers.get(clientId);
        readStepByStep(client, buffer, byteBuffers)
                .flatMap(bytes -> serializer.readObject(bytes, Object.class))
                .ifPresent(object -> {
                    register(selector, client, OP_WRITE);
                    executor.acceptRequest(clientId, object);
                });
    }

    private void run() {
        while (true) {
            select(selector); // Blocking call. The current thread will be blocked till a client connect to server.
            final Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    handleRead(key);
                } else if (key.isWritable()) {
                    handleWrite(key);
                }
                iter.remove();
            }
        }
    }

    private void handleWrite(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final int clientId = client.hashCode();
        executor.getResponse(clientId)
                .flatMap(serializer::writeObject)
                .ifPresent(bytes -> {
                    final int writtenBytes = write(client, wrap(bytes));
                    log.debug("{} bytes've been written to {}", writtenBytes, getRemoteAddress(client));
                    register(selector, client, OP_READ);
                    executor.removeResponse(clientId);
                });
    }
}
