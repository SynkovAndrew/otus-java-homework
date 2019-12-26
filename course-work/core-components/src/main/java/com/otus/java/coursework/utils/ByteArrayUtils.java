package com.otus.java.coursework.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.copyOfRange;

public class ByteArrayUtils {
    public static byte[] BYTE_ARRAY_DELIMITER = {
            (byte) '\t',
            (byte) '\t',
            (byte) '\r',
            (byte) '\r',
            (byte) '\n',
            (byte) '\n',
            (byte) '\r',
            (byte) '\r',
            (byte) '\f',
            (byte) '\f',
            (byte) '\r',
            (byte) '\r'
    };

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

    private static boolean isMatch(final byte[] pattern,
                                   final byte[] input,
                                   final int position) {
        for (int i = 0; i < pattern.length; i++) {
            if (position + i > input.length - 1 || pattern[i] != input[position + i]) {
                return false;
            }
        }
        return true;
    }

    public static List<byte[]> parts(final byte[] input, final int partSize) {
        final var list = new CopyOnWriteArrayList<byte[]>();
        final int partsCount = (int) Math.ceil( (double) input.length / partSize );
        for (int i = 0; i < partsCount; i++) {
            list.add(Arrays.copyOfRange(
                    input,
                    i * partSize,
                    i == partsCount - 1 ? input.length : (i + 1) * partSize
            ));
        }
        return list;
    }

    public static SplitResult split(final byte[] pattern,
                                    final byte[] input) {
        final var chunks = new ArrayList<Chunk>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                final Chunk chunk = Chunk.builder()
                        .bytes(copyOfRange(input, blockStart, i))
                        .isCompleted(true)
                        .isLast(true)
                        .build();
                chunks.add(chunk);
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        final byte[] lastPart = copyOfRange(input, blockStart, input.length);
        if (lastPart.length != 0) {
            final Chunk chunk = Chunk.builder()
                    .bytes(lastPart)
                    .isCompleted(false)
                    .isLast(false)
                    .build();
            chunks.add(chunk);
        }
        return SplitResult.builder()
                .chunks(chunks)
                .build();
    }
}
