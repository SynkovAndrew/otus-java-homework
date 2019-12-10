package com.otus.java.coursework.utils;

import java.util.ArrayList;
import java.util.List;
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

    private static boolean isMatch(final byte[] pattern,
                                   final byte[] input,
                                   final int position) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[position + i]) {
                return false;
            }
        }
        return true;
    }

    public static SplitResult split(final byte[] pattern,
                                    final byte[] input,
                                    final boolean lastChunkComplete) {
        final var chunks = new ArrayList<Chunk>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                final boolean isChunkCompleted = chunks.size() != 0 || lastChunkComplete;
                final Chunk chunk = Chunk.builder()
                        .bytes(copyOfRange(input, blockStart, i))
                        .isCompleted(isChunkCompleted)
                        .isLast(true)
                        .build();
                chunks.add(chunk);
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        boolean lastPartFinished = true;
        final byte[] lastPart = copyOfRange(input, blockStart, input.length);
        if (lastPart.length != 0) {
            lastPartFinished = false;
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
}
