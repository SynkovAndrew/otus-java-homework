package com.otus.java.coursework;

import com.otus.java.coursework.dto.UserDTO;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.serialization.SerializerImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SerializerTest {
    private static Serializer serializer;

    @BeforeAll
    public static void setUp() {
        serializer = new SerializerImpl();
    }

    @Test
    public void writeAndReadObject() {
        final UserDTO user = UserDTO.builder()
                .age(33)
                .firstName("Vlad")
                .lastName("Pervomayski")
                .userId(10001)
                .build();
        final byte[] bytes = serializer.writeObject(user).get();
        final UserDTO restored = serializer.readObject(bytes, UserDTO.class).get();
        assertNotNull(restored);
        assertEquals(10001, restored.getUserId());
        assertEquals("Vlad", restored.getFirstName());
        assertEquals("Pervomayski", restored.getLastName());
        assertEquals(33, restored.getAge());
    }
}
