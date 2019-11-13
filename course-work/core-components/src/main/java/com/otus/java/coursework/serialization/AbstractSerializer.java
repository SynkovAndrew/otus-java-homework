package com.otus.java.coursework.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.java.coursework.dto.BaseDTO;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
public abstract class AbstractSerializer {
    private final Map<String, Class<? extends BaseDTO>> classes;
    private final ObjectMapper mapper;

    protected AbstractSerializer() {
        this.mapper = new ObjectMapper();
        this.classes = concat(of(CreateUserRequestDTO.class, UserDTO.class), getCustomClasses().stream())
                .collect(toMap(Class::getSimpleName, identity()));
    }

    /*
     * Supported types of serializer may be extended with overriding this method.
     * Custom class must implement BaseDTO interface.
     */
    protected abstract Set<Class<? extends BaseDTO>> getCustomClasses();

    public Optional<String> map(final Message<? extends BaseDTO> message) {
        try {
            return ofNullable(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Failed to map object to json", e);
            return empty();
        }
    }

    public Optional<Message<BaseDTO>> map(final String json) {
        final var className = substringBetween(json, "\"clazz\":\"", "\",\"content");
        final var content = substringBeforeLast(substringAfterLast(json, "\"content\":"), "}");
        final Class<? extends BaseDTO> clazz = classes.get(className);
        if (nonNull(clazz)) {
            try {
                final BaseDTO object = mapper.readValue(content, clazz);
                return Optional.of(new Message<>(object));
            } catch (JsonProcessingException e) {
                log.error("Failed to map object to json", e);
                return empty();
            }
        }
        return empty();
    }
}
