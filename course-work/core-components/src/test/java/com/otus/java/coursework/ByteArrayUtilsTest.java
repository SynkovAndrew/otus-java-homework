package com.otus.java.coursework;

import com.otus.java.coursework.utils.ByteArrayUtils;
import com.otus.java.coursework.utils.SplitResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.otus.java.coursework.utils.ByteArrayUtils.BYTE_ARRAY_DELIMITER;
import static com.otus.java.coursework.utils.ByteArrayUtils.fill;
import static org.assertj.core.api.Assertions.assertThat;

public class ByteArrayUtilsTest {

    @Test
    public void splitOnThreeCompletedChunk() {
        final byte[] part1 = "Hello!".getBytes();
        final byte[] part2 = "This is me".getBytes();
        final byte[] part3 = {Integer.valueOf(11).byteValue()};
        final byte[] concatenated = new byte[part1.length + BYTE_ARRAY_DELIMITER.length + part2.length +
                BYTE_ARRAY_DELIMITER.length + part3.length + BYTE_ARRAY_DELIMITER.length];

        fill(concatenated, part1, BYTE_ARRAY_DELIMITER, part2, BYTE_ARRAY_DELIMITER, part3, BYTE_ARRAY_DELIMITER);

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        assertThat(splitResult).isNotNull();
        assertThat(splitResult.getChunks()).hasSize(3);
        assertThat(splitResult.getChunks()).extracting("bytes")
                .containsExactly(part1, part2, part3);
        assertThat(splitResult.getChunks()).extracting("isCompleted")
                .containsExactly(true, true, true);
    }

    @Test
    public void splitOnTwoCompletedAndOneNotCompletedChunk() {
        final byte[] part1 = "Hello!".getBytes();
        final byte[] part2 = "This is me".getBytes();
        final byte[] part3 = "The tree will grow forever!".getBytes();
        final byte[] concatenated = new byte[part1.length + BYTE_ARRAY_DELIMITER.length + part2.length +
                BYTE_ARRAY_DELIMITER.length + part3.length];

        fill(concatenated, part1, BYTE_ARRAY_DELIMITER, part2, BYTE_ARRAY_DELIMITER, part3);

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        assertThat(splitResult).isNotNull();
        assertThat(splitResult.getChunks()).hasSize(3);
        assertThat(splitResult.getChunks()).extracting("bytes")
                .containsExactly(part1, part2, part3);
        assertThat(splitResult.getChunks()).extracting("isCompleted")
                .containsExactly(true, true, false);
        assertThat(splitResult.getChunks()).extracting("isLast")
                .containsExactly(true, true, false);
    }

    @Test
    public void splitOnOneNotCompletedAndTwoCompleted() {
        final byte[] part1 = "Hello!".getBytes();
        final byte[] part2 = "This is me".getBytes();
        final byte[] part3 = "The tree will grow forever!".getBytes();
        final byte[] concatenated = new byte[part1.length + BYTE_ARRAY_DELIMITER.length + part2.length +
                BYTE_ARRAY_DELIMITER.length + part3.length];

        fill(concatenated, part1, BYTE_ARRAY_DELIMITER, part2, BYTE_ARRAY_DELIMITER, part3);

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        assertThat(splitResult).isNotNull();
        assertThat(splitResult.getChunks()).hasSize(3);
        assertThat(splitResult.getChunks()).extracting("bytes")
                .containsExactly(part1, part2, part3);
        assertThat(splitResult.getChunks()).extracting("isCompleted")
                .containsExactly(true, true, false);
        assertThat(splitResult.getChunks()).extracting("isLast")
                .containsExactly(true, true, false);
    }

    @Test
    public void splitWhenDelimiterIsNotFull() {
        final byte[] part1 = "Hello!".getBytes();
        final int middle = BYTE_ARRAY_DELIMITER.length / 2;
        final byte[] partOfDelimiter = Arrays.copyOfRange(BYTE_ARRAY_DELIMITER, 0, middle);
        final byte[] concatenated = new byte[part1.length + middle];

        fill(concatenated, part1, partOfDelimiter);

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        assertThat(splitResult).isNotNull();
        assertThat(splitResult.getChunks()).hasSize(1);
        assertThat(splitResult.getChunks()).extracting("bytes")
                .containsExactly(concatenated);
        assertThat(splitResult.getChunks()).extracting("isCompleted")
                .containsExactly(false);
        assertThat(splitResult.getChunks()).extracting("isLast")
                .containsExactly(false);
    }

    @Test
    public void parts_1() {
        final byte[] bytes = "aaabbbcccddd".getBytes();

        final var parts = ByteArrayUtils.parts(bytes, 3);
        assertThat(parts).isNotNull();
        assertThat(parts).hasSize(4);
        assertThat(parts).containsExactly("aaa".getBytes(), "bbb".getBytes(), "ccc".getBytes(), "ddd".getBytes());
    }

    @Test
    public void parts_2() {
        final byte[] bytes = "abcdefg".getBytes();

        final var parts = ByteArrayUtils.parts(bytes, 5);
        assertThat(parts).isNotNull();
        assertThat(parts).hasSize(2);
        assertThat(parts).containsExactly("abcde".getBytes(), "fg".getBytes());
    }
}
