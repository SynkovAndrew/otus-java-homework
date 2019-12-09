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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import static com.otus.java.coursework.utils.ByteArrayUtils.*;
import static java.nio.ByteBuffer.wrap;

@Slf4j
@Component
public class Server {
    private final static int INITIAL_BYTE_BUFFER_SIZE = 128;
    private final ServerRequestExecutor executor;
    private final ExecutorService serverRunner;
    private final Serializer serializer;
    private final ConcurrentMap<Integer, ByteBuffer> byteBuffers;
    private final ConcurrentMap<Integer, List<byte[]>> byteArrays;
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
        this.byteArrays = new ConcurrentHashMap<>();
        this.executor = executor;
        this.serverRunner = serverRunner;
        this.serializer = serializer;
    }

    private void accept() {
        SocketChannelUtils.accept(selector, serverSocketChannel).ifPresent(client -> {
            byteBuffers.put(client.hashCode(), ByteBuffer.allocate(INITIAL_BYTE_BUFFER_SIZE));
            byteArrays.put(client.hashCode(), new CopyOnWriteArrayList<>());
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
        final int readBytes = SocketChannelUtils.read(client, buffer);
        // in case of result is equal to -1 the connection's closed from client side
        if (readBytes == -1) {
            log.info("{} connection's been closed", SocketChannelUtils.getRemoteAddress(client).get());
            byteBuffers.remove(clientId);
            SocketChannelUtils.close(client);
        }
        if (readBytes > 0) {
            SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
            buffer.flip();
            log.info("{} bytes've been read from {}", readBytes, SocketChannelUtils.getRemoteAddress(client).get());

            // take all bytes which has just been read from socket channel
            final byte[] bytes = buffer.array();

            // split all read bytes by defined delimiter
            final SplitResult splitResult = split(BYTE_ARRAY_DELIMITER, bytes);
            final int partCount = splitResult.getParts().size();

            // if there is no delimiter in the end of last part bytes, then the last byte part is not completely received
            if (!splitResult.isLastPartFinished()) {
                final byte[] lastPart = splitResult.getParts().get(partCount - 1);
                byteArrays.get(clientId).add(lastPart);
            }

            // all the byte arrays here are complete, so they can be deserialized
            for (int i = 0; i < partCount - 1; i++) {
                final byte[] part = splitResult.getParts().get(i);
                serializer.readObject(part, Object.class)
                        .ifPresent(object -> executor.acceptRequest(clientId, object));
            }
        }
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

    private void write(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        executor.getResponse(client.hashCode())
                .flatMap(serializer::writeObject)
                .ifPresent(bytes -> {
                    final ByteBuffer buffer = byteBuffers.get(client.hashCode());
                    buffer.clear();
                    buffer.put(wrap(bytes));
                    buffer.flip();
                    final int writtenBytes = SocketChannelUtils.write(client, buffer);
                    log.info("{} bytes've been written to {}",
                            writtenBytes, SocketChannelUtils.getRemoteAddress(client).get());
                    if (!buffer.hasRemaining()) {
                        buffer.compact();
                        SocketChannelUtils.register(selector, client, SelectionKey.OP_READ);
                    }
                });
    }
}
