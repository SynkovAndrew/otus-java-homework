package com.otus.java.coursework.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.java.coursework.dto.BaseDTO;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.*;
import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
public class Serializer {
    private final static Map<String, Class<? extends BaseDTO>> classes = Map.of(
            CreateUserRequestDTO.class.getSimpleName(), CreateUserRequestDTO.class,
            UserDTO.class.getSimpleName(), UserDTO.class
    );
    private final static ObjectMapper mapper = new ObjectMapper();

    public static Optional<String> map(final Message<? extends BaseDTO> message) {
        try {
            return ofNullable(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Failed to map object to json", e);
            return empty();
        }
    }

    public static Optional<Message<BaseDTO>> map(final String json) {
        final var className = substringBetween(json, "\"clazz\":\"", "\",\"content");
        final var content = substringBeforeLast(substringAfterLast(json, "\"content\":"), "}");

        final Class<? extends BaseDTO> clazz = classes.get(className);
        try {
            final BaseDTO object = mapper.readValue(content, clazz);
            return of(new Message<>(object));
        } catch (JsonProcessingException e) {
            log.error("Failed to map object to json", e);
            return empty();
        }
    }
}
