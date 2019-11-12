package com.otus.java.coursework.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.substringBetween;

@Slf4j
public class Serializer {
    private final static Map<String, Class<?>> classes = Map.of(
            CreateUserRequestDTO.class.getSimpleName(), CreateUserRequestDTO.class,
            UserDTO.class.getSimpleName(), UserDTO.class
    );
    private final static ObjectMapper mapper = new ObjectMapper();

    private static Optional<String> map(final Message<Object> message) {
        try {
            return Optional.ofNullable(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Failed to map object to json", e);
            return Optional.empty();
        }
    }

    static Message<Object> mapToObject(final String json) {
        final var className = substringBetween(json, "\"clazz\":\"", "\",\"content");
        final var content = substringBetween(json, "\"content\":", ",\"");

/*        final Class<Object> clazz = classes.get(className);
        final Object object = mapper.readValues(content, clazz);
        return new Message<>(className, object, instanceId);*/
        return null;
    }
}
