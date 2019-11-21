package com.otus.java.coursework.server;

import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.server.executor.ServerRequestExecutor;
import com.otus.java.coursework.server.socket.SocketServerComponent;
import com.otus.java.coursework.utils.Mapper;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.nio.ByteBuffer.wrap;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Slf4j
@Component
public class Server {
    private final static byte END_OF_MESSAGE = '\n';
    private final SocketServerComponent socketServerComponent;
    private final ServerRequestExecutor executor;
    private final Serializer serializer;
    private final ExecutorService serverRunner;
    private final Map<Integer, ByteBuffer> socketChannels;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public Server(final SocketServerComponent socketServerComponent,
                  final ServerRequestExecutor executor,
                  final Serializer serializer) {
        this.socketServerComponent = socketServerComponent;
        this.executor = executor;
        this.serializer = serializer;
        this.socketChannels = new ConcurrentHashMap<>();
        this.serverRunner = newSingleThreadExecutor();
    }

    @PostConstruct
    public void init() {
        log.info("Server's been started at {}:{}", host, port);
    }

    private void read(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final ByteBuffer buffer = socketChannels.get(client.hashCode());
        final int readBytes = SocketChannelUtils.read(client, buffer);

        // in case of result is equal to -1 the connection's closed from client side
        if (readBytes == -1) {
            log.info("{} connection's been closed", SocketChannelUtils.getRemoteAddress(client).get());
            socketChannels.remove(client.hashCode());
            SocketChannelUtils.close(client);
        }

        if (readBytes > 0 && END_OF_MESSAGE == buffer.get(buffer.position() - 1)) {
            SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
            buffer.flip();
            final var json = new String(buffer.array(), buffer.position(), buffer.limit())
                    .replaceAll("\n", "");
            serializer.map(json).ifPresent(message -> {
                executor.acceptRequest(client.hashCode(), message.getContent());
                log.info("Message {} from {} 's been received", message, SocketChannelUtils.getRemoteAddress(client).get());
            });
        }
    }


    private void write(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        executor.getResponse(client.hashCode()).flatMap(Mapper::map).ifPresent(json -> {
            final ByteBuffer buffer = socketChannels.get(client.hashCode());
            buffer.clear();
            buffer.put(wrap(json.getBytes()));
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
