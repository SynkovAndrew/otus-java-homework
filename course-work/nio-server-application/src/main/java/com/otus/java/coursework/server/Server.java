package com.otus.java.coursework.server;

import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.server.executor.ServerRequestExecutor;
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
    private final ServerRequestExecutor executor;
    private final Selector selector;
    private final Serializer serializer;
    private final ExecutorService serverRunner;
    private final ServerSocketChannel serverSocketChannel;
    private final Map<Integer, ByteBuffer> socketChannels;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;

    public Server(final ServerRequestExecutor executor,
                  final Serializer serializer) throws IOException {
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.socketChannels = new ConcurrentHashMap<>();
        this.executor = executor;
        this.serializer = serializer;
        this.serverRunner = newSingleThreadExecutor();
    }

    private void accept() {
        SocketChannelUtils.accept(selector, serverSocketChannel).ifPresent(client -> {
            socketChannels.put(client.hashCode(), ByteBuffer.allocate(1024));
            log.info("Client {}'s been connected to server", SocketChannelUtils.getRemoteAddress(client).get());
        });
    }

    @PreDestroy
    public void destroy() {
        SocketChannelUtils.destroy(selector, serverSocketChannel);
    }

    @PostConstruct
    public void init() throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverRunner.execute(this::run);
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
