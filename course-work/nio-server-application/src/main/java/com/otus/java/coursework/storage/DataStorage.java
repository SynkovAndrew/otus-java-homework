package com.otus.java.coursework.storage;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class DataStorage {
    private final ConcurrentMap<Integer, ByteBuffer> buffers;

    public DataStorage() {
        this.buffers = new ConcurrentHashMap<>();
    }

    public void putBytes(final int id, final byte[] bytes) {
        if (buffers.containsKey(id)) {
            final ByteBuffer byteBuffer = buffers.get(id);
            byteBuffer.add(bytes);
        } else {
            final ByteBuffer byteBuffer = new ByteBuffer();
            byteBuffer.add(bytes);
            buffers.put(id, byteBuffer);
        }
    }
}
