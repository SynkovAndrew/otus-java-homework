package com.otus.java.projectwork.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NioSocketServer {
    private final static String END_OF_MESSAGE = "EOM";
    private final Selector selector;
    private final ServerSocketChannel serverSocket;
    private final ByteBuffer buffer;

    public NioSocketServer(final String host, final int port) throws IOException {
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
        this.serverSocket.bind(new InetSocketAddress(host, port));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        this.buffer = ByteBuffer.allocate(256);
    }

    public void start() throws IOException {
        log.info("nio socket server's been started");
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    log.info("connection from client: {}' s been accepted", client);
                }
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    client.read(buffer);
                    final String message = new String(buffer.array());
                    log.info("message: {}' s been received", message);

                    if (message.trim().equals(END_OF_MESSAGE)) {
                        client.close();
                        log.info("Not accepting client messages anymore");
                    }

                    buffer.flip();
                    client.write(buffer);
                    buffer.clear();
                }
                iter.remove();
            }
        }
    }
}
