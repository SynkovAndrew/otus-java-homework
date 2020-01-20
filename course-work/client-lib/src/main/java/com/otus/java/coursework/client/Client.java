package com.otus.java.coursework.client;

import com.otus.java.coursework.dto.ByteMessage;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.utils.SocketChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.otus.java.coursework.utils.SocketChannelUtils.*;
import static java.nio.ByteBuffer.wrap;
import static java.nio.channels.SelectionKey.OP_READ;

@Slf4j
public class Client implements AutoCloseable {
    private final Serializer serializer;
    private final SocketChannel socketChannel;
    private final Selector selector;
    private final ByteBuffer buffer;

    public Client(final String host,
                  final int port,
                  final Serializer serializer) throws FailedToCreateClientException, IOException {
        this.socketChannel = openSocketChannel(host, port)
                .orElseThrow(FailedToCreateClientException::new);
        this.socketChannel.configureBlocking(false);
        this.serializer = serializer;
        this.selector = Selector.open();
        this.buffer = ByteBuffer.allocate(16);
    }

    @Override
    public void close() {
        SocketChannelUtils.close(socketChannel);
    }

    public void send(final Object object) {
        serializer.writeObject(object)
                .map(ByteMessage::new)
                .flatMap(serializer::writeObject)
                .ifPresent(bytes -> {
                    write(socketChannel, wrap(bytes));
                    register(selector, socketChannel, OP_READ);
                    while (true) {
                        select(selector);
                        final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                        while (selectedKeys.hasNext()) {
                            SelectionKey key = selectedKeys.next();
                            if (key.isReadable()) {
                                SocketChannel client = (SocketChannel) key.channel();
                                readStepByStep(client, buffer)
                                        .flatMap(serializer::readObject)
                                        .ifPresent(readObject -> {
                                            final Object o = serializer.readObject(((ByteMessage) readObject).getContent()).get();
                                            log.info("Response from server's been received: {}", o);
                                        });
                                return;
                            }
                            selectedKeys.remove();
                        }
                        return;
                    }
                });
    }
}
