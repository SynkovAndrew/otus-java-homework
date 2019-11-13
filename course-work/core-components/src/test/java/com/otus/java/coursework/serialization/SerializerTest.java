package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.BaseDTO;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SerializerTest {
    private static Serializer serializer;

    @BeforeAll
    public static void setUp() {
        serializer = new Serializer();
    }

    @Test
    public void mapJsonToCreateUserRequest() {
        final String json = "{\"clazz\":\"CreateUserRequestDTO\",\"content\":{\"age\":34,\"name\":\"Nikolai\"}}";
        final Message<BaseDTO> message = serializer.map(json).get();
        final CreateUserRequestDTO content = (CreateUserRequestDTO) message.getContent();
        assertNotNull(message);
        assertEquals("CreateUserRequestDTO", message.getClazz());
        assertNotNull(content);
        assertEquals(34, content.getAge());
        assertEquals("Nikolai", content.getName());
    }

    @Test
    public void mapJsonToUser() {
        final String json = "{\"clazz\":\"UserDTO\",\"content\":{\"age\":23,\"name\":\"Vasili\",\"userId\":1001}}";
        final Message<BaseDTO> message = serializer.map(json).get();
        final UserDTO content = (UserDTO) message.getContent();
        assertNotNull(message);
        assertEquals("UserDTO", message.getClazz());
        assertNotNull(content);
        assertEquals(23, content.getAge());
        assertEquals("Vasili", content.getName());
        assertEquals(1001, content.getUserId());
    }

    @Test
    public void mapUserToJson() {
        final String expected = "{\"clazz\":\"UserDTO\",\"content\":{\"age\":77,\"name\":\"Sam\",\"userId\":1}}";
        final UserDTO user = UserDTO.builder().age(77).name("Sam").userId(1L).build();
        final Message<UserDTO> message = new Message<>(user);
        final Optional<String> optional = serializer.map(message);
        assertTrue(optional.isPresent());
        assertEquals(expected, optional.get());
    }

    @Test
    public void mapCreateUserRequestToJson() {
        final String expected = "{\"clazz\":\"CreateUserRequestDTO\",\"content\":{\"age\":24,\"name\":\"Tom\"}}";
        final CreateUserRequestDTO request = CreateUserRequestDTO.builder().age(24).name("Tom").build();
        final Message<CreateUserRequestDTO> message = new Message<>(request);
        final Optional<String> optional = serializer.map(message);
        assertTrue(optional.isPresent());
        assertEquals(expected, optional.get());
    }
}
