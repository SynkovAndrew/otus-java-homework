package com.otus.java.projectwork.client;

import java.io.IOException;

public class NioSocketClientMain {
    public static void main(String[] args) throws IOException {
        final NioSocketClient client = new NioSocketClient("localhost", 4444);
        client.sendMessage("Hello!");
    }
}
