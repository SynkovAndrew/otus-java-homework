package com.otus.java.coursework.server;

import com.otus.java.coursework.executor.ServerRequestExecutor;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.Chunk;
import com.otus.java.coursework.utils.SocketChannelUtils;
import com.otus.java.coursework.utils.SplitResult;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.otus.java.coursework.utils.ByteArrayUtils.*;
import static java.nio.ByteBuffer.wrap;

@Slf4j
@Component
public class Server {
    private final static int INITIAL_BYTE_BUFFER_SIZE = 512;
    private final ServerRequestExecutor executor;
    private final ExecutorService serverRunner;
    private final Serializer serializer;
    private final ConcurrentMap<Integer, ByteBuffer> byteBuffers;
    private final ConcurrentMap<Integer, List<Chunk>> uncompletedChunkMap;
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
        this.uncompletedChunkMap = new ConcurrentHashMap<>();
        this.executor = executor;
        this.serverRunner = serverRunner;
        this.serializer = serializer;
    }

    private void accept() {
        SocketChannelUtils.accept(selector, serverSocketChannel).ifPresent(client -> {
            byteBuffers.put(client.hashCode(), ByteBuffer.allocate(INITIAL_BYTE_BUFFER_SIZE));
            uncompletedChunkMap.put(client.hashCode(), new CopyOnWriteArrayList<>());
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
        } else if (readBytes > 0) {
            buffer.flip();
            log.info("{} bytes've been read from {}", readBytes, SocketChannelUtils.getRemoteAddress(client).get());
            // take all bytes which has just been read from socket channel
            final byte[] bytes = buffer.array();
            // get uncompleted chunks
            final List<Chunk> uncompletedChunks = this.uncompletedChunkMap.get(clientId);
            // split all read bytes by defined delimiter
            final SplitResult splitResult = split(BYTE_ARRAY_DELIMITER, bytes, uncompletedChunks.size() == 0);
            final List<Chunk> currentIterationChunks = splitResult.getChunks();
            final int currentIterationChunksCount = currentIterationChunks.size();

            if (currentIterationChunksCount > 0) {
                final Chunk firstChunk = currentIterationChunks.get(0);
                // in first received chunk is marked as not completed and last, so it should be joined
                // with collected uncompleted chunks in order to compose completed one
                if (!firstChunk.isCompleted() && firstChunk.isLast()) {
                    final int completeChunkSize = uncompletedChunks.stream()
                            .mapToInt(chunk -> chunk.getBytes().length)
                            .sum() + firstChunk.getBytes().length;
                    final List<byte[]> byteArrayList = Stream.concat(uncompletedChunks.stream(), Stream.of(firstChunk))
                            .map(Chunk::getBytes)
                            .collect(Collectors.toList());
                    final byte[] completeChunkBytes = new byte[completeChunkSize];
                    fill(completeChunkBytes, byteArrayList);

                    // once completed chunk is composed it passes to serializer
                    serializer.readObject(completeChunkBytes, Object.class)
                            .ifPresent(object -> executor.acceptRequest(clientId, object));
                    // clear uncompleted chunks
                    uncompletedChunks.clear();
                    SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
                }

                // if there is no delimiter in the end of last chunk bytes, then tis chunk is not completely received
                final Chunk lastChunk = currentIterationChunks.get(currentIterationChunksCount - 1);
                if (!lastChunk.isCompleted() && !firstChunk.isLast()) {
                    uncompletedChunks.add(lastChunk);
                }

                // all the chunks here are completed, so they can passed ro serializer
                currentIterationChunks.stream()
                        .filter(Chunk::isCompleted)
                        .forEach(chunk -> serializer.readObject(chunk.getBytes(), Object.class)
                                .ifPresent(object -> {
                                    SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
                                    executor.acceptRequest(clientId, object);
                                }));
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
