package com.otus.java.projectwork.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class NioSocketClient {
    private SocketChannel client;
    private ByteBuffer buffer;

    public NioSocketClient(final String host, final int port) throws IOException {
        this.client = SocketChannel.open(new InetSocketAddress(host, port));
        this.buffer = ByteBuffer.allocate(256);
    }

    public String sendMessage(final String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String message = null;
        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            message = new String(buffer.array()).trim();
            log.info("message: {}'s been received from server", message);
            buffer.clear();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return message;

    }

    public void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }
}
