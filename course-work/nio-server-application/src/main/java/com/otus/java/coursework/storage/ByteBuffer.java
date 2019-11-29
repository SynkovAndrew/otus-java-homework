package com.otus.java.coursework.storage;

import java.util.List;

import static org.apache.commons.compress.utils.Lists.newArrayList;

public class ByteBuffer {
    private final List<Byte> bytes;

    public ByteBuffer() {
        this.bytes = newArrayList();
    }

    public void add(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            this.bytes.add(i, bytes[i]);
        }
    }

    public byte[] getBytes() {
        final int size = this.bytes.size();
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = this.bytes.get(i);
        }
        return bytes;
    }
}
