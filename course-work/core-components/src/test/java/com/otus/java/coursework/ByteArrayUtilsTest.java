package com.otus.java.coursework;

import com.otus.java.coursework.utils.ByteArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otus.java.coursework.utils.ByteArrayUtils.BYTE_ARRAY_DELIMITER;
import static com.otus.java.coursework.utils.ByteArrayUtils.fill;
import static org.junit.jupiter.api.Assertions.*;

public class ByteArrayUtilsTest {

    @Test
    public void split_1() {
        final byte[] part1 = "Hello!".getBytes();
        final byte[] part2 = "This is me".getBytes();
        final byte[] part3 = {Integer.valueOf(11).byteValue()};
        final byte[] concatenated = new byte[part1.length + BYTE_ARRAY_DELIMITER.length + part2.length +
                BYTE_ARRAY_DELIMITER.length + part3.length + BYTE_ARRAY_DELIMITER.length];

        fill(concatenated, part1, BYTE_ARRAY_DELIMITER, part2, BYTE_ARRAY_DELIMITER, part3, BYTE_ARRAY_DELIMITER);

        final ByteArrayUtils.SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        final List<byte[]> parts = splitResult.getParts();
        final boolean lastPartFinished = splitResult.isLastPartFinished();
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertArrayEquals(part1, parts.get(0));
        assertArrayEquals(part2, parts.get(1));
        assertArrayEquals(part3, parts.get(2));
        assertTrue(lastPartFinished);
    }

    @Test
    public void split_2() {
        final byte[] part1 = "Hello!".getBytes();
        final byte[] part2 = "This is me".getBytes();
        final byte[] part3 = "The tree will grow forever!".getBytes();
        final byte[] concatenated = new byte[part1.length + BYTE_ARRAY_DELIMITER.length + part2.length +
                BYTE_ARRAY_DELIMITER.length + part3.length];

        fill(concatenated, part1, BYTE_ARRAY_DELIMITER, part2, BYTE_ARRAY_DELIMITER, part3);

        final ByteArrayUtils.SplitResult splitResult = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, concatenated);
        final List<byte[]> parts = splitResult.getParts();
        final boolean lastPartFinished = splitResult.isLastPartFinished();
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertArrayEquals(part1, parts.get(0));
        assertArrayEquals(part2, parts.get(1));
        assertArrayEquals(part3, parts.get(2));
        assertFalse(lastPartFinished);
    }
}
