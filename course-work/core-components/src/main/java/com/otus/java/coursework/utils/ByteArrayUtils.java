package com.otus.java.coursework.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteArrayUtils {
    public static void fill(final byte[] target,
                            final byte[]... sources) {
        final AtomicInteger i = new AtomicInteger(0);
        for (final byte[] source : sources) {
            fill(target, source, i);
        }
    }

    public static void fill(final byte[] target,
                            final List<byte[]> sources) {
        final AtomicInteger i = new AtomicInteger(0);
        for (final byte[] source : sources) {
            fill(target, source, i);
        }
    }

    private static void fill(final byte[] target,
                             final byte[] source,
                             final AtomicInteger position) {
        for (byte b : source) {
            target[position.getAndIncrement()] = b;
        }
    }

    public static List<byte[]> parts(final byte[] input, final int partSize) {
        final var list = new CopyOnWriteArrayList<byte[]>();
        final int partsCount = (int) Math.ceil((double) input.length / partSize);
        for (int i = 0; i < partsCount; i++) {
            list.add(Arrays.copyOfRange(
                    input,
                    i * partSize,
                    i == partsCount - 1 ? input.length : (i + 1) * partSize
            ));
        }
        return list;
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
