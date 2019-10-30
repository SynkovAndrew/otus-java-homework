package com.otus.java.projectwork.server;

import java.io.IOException;

public class NioSocketServerMain {
    public static void main(String[] args) throws IOException {
        final NioSocketServer socketServer = new NioSocketServer("localhost", 4444);
        socketServer.start();
    }
}
