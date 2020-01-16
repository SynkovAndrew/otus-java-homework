package com.otus.java.coursework.server;

import com.otus.java.coursework.executor.ServerRequestExecutor;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.ByteArrayUtils;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import static de.flapdoodle.embed.process.collections.Collections.newArrayList;
import static java.nio.ByteBuffer.wrap;
import static java.util.Arrays.copyOf;

@Slf4j
@Component
public class Server {
    private final static int INITIAL_BYTE_BUFFER_SIZE = 16;
    private final ConcurrentMap<Integer, ByteBuffer> byteBuffers;
    private final ServerRequestExecutor executor;
    private final Serializer serializer;
    private final ExecutorService serverRunner;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public Server(final ServerRequestExecutor executor,
                  final ExecutorService serverRunner,
                  final Serializer serializer) {
        this.byteBuffers = new ConcurrentHashMap<>();
        this.executor = executor;
        this.serverRunner = serverRunner;
        this.serializer = serializer;
    }

    private void accept() {
        SocketChannelUtils.accept(selector, serverSocketChannel).ifPresent(client -> {
            final var clientId = client.hashCode();
            byteBuffers.put(clientId, ByteBuffer.allocate(INITIAL_BYTE_BUFFER_SIZE));
            log.info("Client {}'s been connected to server", SocketChannelUtils.getRemoteAddress(client).get());
        });
    }

    @PreDestroy
    public void destroy() {
        SocketChannelUtils.destroy(selector, serverSocketChannel);
    }

    @PostConstruct
    public void init() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverRunner.execute(this::run);
        log.info("Server's been started at {}:{}", host, port);
    }

    private void read(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final int clientId = client.hashCode();
        final ByteBuffer buffer = byteBuffers.get(clientId);

        final List<byte[]> byteArrays = newArrayList();
        int readBytes = SocketChannelUtils.read(client, buffer);
        while (readBytes > 0) {
            buffer.flip();
            log.info("{} bytes've been read from {}", readBytes, SocketChannelUtils.getRemoteAddress(client).get());
            final byte[] receivedBytes = buffer.array();
            byteArrays.add(copyOf(receivedBytes, receivedBytes.length));
            buffer.flip();
            buffer.clear();
            readBytes = SocketChannelUtils.read(client, buffer);
        }
        final byte[] bytes = ByteArrayUtils.flatMap(byteArrays);
        serializer.readObject(bytes)
                .ifPresent(object -> {
                    SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
                    executor.acceptRequest(clientId, object);
                });
/*        if (readBytes == -1) {
            // in case of result is equal to -1 the connection's closed from client side
            log.info("{} connection's been closed", SocketChannelUtils.getRemoteAddress(client).get());
            byteBuffers.remove(clientId);
            SocketChannelUtils.close(client);
        }*/
    }

    private void run() {
        while (true) {
            SocketChannelUtils.select(selector); // Blocking call. The current thread will be blocked till a client connect to server.
            final Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    accept();
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
                iter.remove();
            }
        }
    }

    private void write(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final int clientId = client.hashCode();
        executor.getResponse(clientId)
                .flatMap(serializer::writeObject)
                .ifPresent(bytes -> {
/*                    final ByteBuffer buffer = byteBuffers.get(client.hashCode());
                    for (byte[] part : ByteArrayUtils.parts(bytes, INITIAL_BYTE_BUFFER_SIZE)) {
                        buffer.clear();
                        buffer.put(wrap(part));
                        buffer.flip();
                        final int writtenBytes = SocketChannelUtils.write(client, buffer);
                        log.info("{} bytes've been written to {}",
                                writtenBytes, SocketChannelUtils.getRemoteAddress(client).get());
                        if (!buffer.hasRemaining()) {
                            buffer.compact();
                        }
                    }*/
                    final int writtenBytes = SocketChannelUtils.write(client, wrap(bytes));
                    log.info("{} bytes've been written to {}",
                            writtenBytes, SocketChannelUtils.getRemoteAddress(client).get());
                    SocketChannelUtils.register(selector, client, SelectionKey.OP_READ);
                    executor.removeResponse(clientId);
                });
    }
}
