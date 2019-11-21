package com.otus.java.coursework.server.socket;

import com.otus.java.coursework.server.exchange.ExchangeComponent;
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

import static java.nio.ByteBuffer.wrap;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
public class SocketServerComponent {
    private final static byte END_OF_MESSAGE = '\n';
    private final Map<Integer, ByteBuffer> socketChannels;
    private final ExchangeComponent exchangeComponent;
    @Value("${server.socket.host}")
    private String host;
    @Value("${server.socket.port}")
    private int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public SocketServerComponent(final ExchangeComponent exchangeComponent) {
        this.socketChannels = new ConcurrentHashMap<>();
        this.exchangeComponent = exchangeComponent;
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
        log.info("Server's been started at {}:{}", host, port);
    }

    private void accept() {
        SocketChannelUtils.accept(selector, serverSocketChannel).ifPresent(client -> {
            socketChannels.put(client.hashCode(), ByteBuffer.allocate(1024));
            log.info("Client {}'s been connected to server", SocketChannelUtils.getRemoteAddress(client).get());
        });
    }

    private void read(final SelectionKey key) {
        final SocketChannel client = (SocketChannel) key.channel();
        final int clientId = client.hashCode();
        final ByteBuffer buffer = socketChannels.get(clientId);
        final int readBytes = SocketChannelUtils.read(client, buffer);

        // in case of result is equal to -1 the connection's closed from client side
        if (readBytes == -1) {
            log.info("{} connection's been closed", SocketChannelUtils.getRemoteAddress(client).get());
            socketChannels.remove(clientId);
            SocketChannelUtils.close(client);
        }

        if (readBytes > 0 && END_OF_MESSAGE == buffer.get(buffer.position() - 1)) {
            SocketChannelUtils.register(selector, client, SelectionKey.OP_WRITE);
            buffer.flip();
            final String jsonMessage = getJsonMessage(buffer);
            exchangeComponent.putInInbox(clientId, jsonMessage);
            log.info("Message {}'s been received from {}", jsonMessage, SocketChannelUtils.getRemoteAddress(client).get());
        }
    }

    public void run() {
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
        final int clientId = client.hashCode();
        ofNullable(exchangeComponent.getFromOutbox(clientId)).ifPresent(json -> {
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

    private String getJsonMessage(final ByteBuffer buffer) {
        return new String(buffer.array(), buffer.position(), buffer.limit())
                .replaceAll("\n", "");
    }
}
