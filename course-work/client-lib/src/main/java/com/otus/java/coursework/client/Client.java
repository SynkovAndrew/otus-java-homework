package com.otus.java.coursework.client;

import com.otus.java.coursework.dto.StringMessage;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.ByteArrayUtils;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import static com.otus.java.coursework.utils.SocketChannelUtils.openSocketChannel;
import static com.otus.java.coursework.utils.SocketChannelUtils.write;
import static java.nio.ByteBuffer.wrap;
import static java.nio.channels.SelectionKey.OP_READ;
import static org.assertj.core.util.Lists.newArrayList;

@Slf4j
public class Client implements AutoCloseable {
    private final Serializer serializer;
    private final SocketChannel socketChannel;
    private final Selector selector;
    private final ByteBuffer byteBuffer;

    public Client(final String host,
                  final int port,
                  final Serializer serializer) throws FailedToCreateClientException, IOException {
        this.socketChannel = openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.socketChannel.configureBlocking(false);

        this.serializer = serializer;
        this.selector = Selector.open();
        this.byteBuffer = ByteBuffer.allocate(16);
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final StringMessage message) {
        serializer.writeObject(message).ifPresent(bytes -> {
            final var buffer = wrap(bytes);
            write(socketChannel, buffer);
/*            SocketChannelUtils.register(selector, socketChannel, OP_READ);

            while (true) {
                if (SocketChannelUtils.select(selector) > 0) {
                    final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        SelectionKey key = selectedKeys.next();
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();

                            final List<byte[]> byteArrays = newArrayList();
                            int readBytes = SocketChannelUtils.read(socketChannel, buffer);
                            while (readBytes > 0) {
                                buffer.flip();
                                log.info("{} bytes've been read from {}", readBytes,
                                        SocketChannelUtils.getRemoteAddress(socketChannel).get());
                                final byte[] receivedBytes = buffer.array();
                                byteArrays.add(receivedBytes);
                                buffer.flip();
                                buffer.clear();
                                readBytes = SocketChannelUtils.read(socketChannel, buffer);
                            }
                            if (readBytes == -1) {
                                // in case of result is equal to -1 the connection's closed from client side
                                log.info("{} connection's been closed",
                                        SocketChannelUtils.getRemoteAddress(socketChannel).get());
                                SocketChannelUtils.close(socketChannel);
                            }

                            final byte[] concatBytes = ByteArrayUtils.flatMap(byteArrays);
                            serializer.readObject(concatBytes)
                                    .ifPresent(stringMessage -> {
                                        //SocketChannelUtils.register(selector, socketChannel, SelectionKey.OP_WRITE);
                                        log.info("Response from server's been received: {}", stringMessage);
                                    });
                            return;
                        }
                        selectedKeys.remove();
                    }
                }
            }*/
        });
    }
}
