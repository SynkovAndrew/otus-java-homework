package com.otus.java.coursework.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                                    final byte[] input) {
        final var parts = new ArrayList<byte[]>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                parts.add(copyOfRange(input, blockStart, i));
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        boolean lastPartFinished = true;
        final byte[] lastPart = copyOfRange(input, blockStart, input.length);
        if (lastPart.length != 0) {
            lastPartFinished = false;
            parts.add(lastPart);
        }
        return SplitResult.builder()
                .parts(parts)
                .lastPartFinished(lastPartFinished)
                .build();
    }

    public static void fill(final byte[] target,
                            final byte[]... sources) {
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitResult {
        private List<byte[]> parts;
        private boolean lastPartFinished;
    }
}
