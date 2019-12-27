package com.otus.java.coursework;

import com.otus.java.coursework.dto.UserDTO;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.serialization.SerializerImpl;
import com.otus.java.coursework.utils.ByteArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otus.java.coursework.utils.ByteArrayUtils.BYTE_ARRAY_DELIMITER;
import static com.otus.java.coursework.utils.ByteArrayUtils.fill;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.assertj.core.api.Assertions.assertThat;

public class ComplexTest {
    private static Serializer serializer;

    @BeforeAll
    public static void setUp() {
        serializer = new SerializerImpl();
    }

    @Test
    public void serializePartsJoinSplitDeserializeTest() {
        final var user = UserDTO.builder()
                .age(23)
                .firstName("Andrew")
                .lastName("Petrov")
                .userId(10001)
                .build();
        final byte[] bytes = serializer.writeObject(user).get();
        final List<byte[]> parts = ByteArrayUtils.parts(bytes, 64);
        final int allBytesSize = parts.stream().mapToInt(b -> b.length).sum() + BYTE_ARRAY_DELIMITER.length;
        final byte[] allBytes = new byte[allBytesSize];
        fill(allBytes, concat(parts.stream(), of(BYTE_ARRAY_DELIMITER)).collect(toList()));
        final byte[] splitBytes = ByteArrayUtils.split(BYTE_ARRAY_DELIMITER, allBytes).getChunks().get(0).getBytes();
        final var result = serializer.readObject(splitBytes, UserDTO.class).get();
        assertThat(result).isNotNull();
        assertThat(result.getAge()).isEqualTo(23);
        assertThat(result.getFirstName()).isEqualTo("Andrew");
        assertThat(result.getLastName()).isEqualTo("Petrov");
        assertThat(result.getUserId()).isEqualTo(10001);
    }
}
