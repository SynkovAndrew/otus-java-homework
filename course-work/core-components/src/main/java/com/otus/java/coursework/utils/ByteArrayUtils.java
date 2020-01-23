package com.otus.java.coursework.utils;

import java.util.List;

public class ByteArrayUtils {
    public static void fill(final byte[] target,
                            final List<byte[]> sources) {
        int i = 0;
        for (final byte[] source : sources) {
            for (byte b : source) {
                target[i++] = b;
            }
        }
    }

    public static byte[] flatMap(final List<byte[]> byteArrays) {
        final int allBytesSize = byteArrays.stream()
                .mapToInt(bytes -> bytes.length)
                .sum();
        final byte[] allBytes = new byte[allBytesSize];
        fill(allBytes, byteArrays);
        return allBytes;
    }
}
