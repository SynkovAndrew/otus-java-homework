package com.otus.java.coursework.service;

import java.util.List;

public interface ByteProcessorService {
    List<byte[]> getCompleteByteSets(int clientId, byte[] receivedBytes);

    void initializeChunk(int clientId);
}
