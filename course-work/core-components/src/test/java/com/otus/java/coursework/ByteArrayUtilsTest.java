package com.otus.java.coursework;

import com.otus.java.coursework.utils.ByteArrayUtils;
import com.otus.java.coursework.utils.SplitResult;
import org.junit.jupiter.api.Test;

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

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated, true);
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

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated, true);
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

        final SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated, false);
        assertThat(splitResult).isNotNull();
        assertThat(splitResult.getChunks()).hasSize(3);
        assertThat(splitResult.getChunks()).extracting("bytes")
                .containsExactly(part1, part2, part3);
        assertThat(splitResult.getChunks()).extracting("isCompleted")
                .containsExactly(false, true, false);
        assertThat(splitResult.getChunks()).extracting("isLast")
                .containsExactly(true, true, false);
    }
}
