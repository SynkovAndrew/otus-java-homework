package com.otus.java.projectwork.nioserver.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.java.projectwork.nioserver.domain.User;
import com.otus.java.projectwork.nioserver.dto.CreateUserRequestDTO;
import com.otus.java.projectwork.nioserver.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Slf4j
public class Mapper {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static User map(final CreateUserRequestDTO source) {
        return User.builder()
                .age(source.getAge())
                .name(source.getName())
                .build();
    }

    public static UserDTO map(final User source) {
        return UserDTO.builder()
                .age(source.getAge())
                .name(source.getName())
                .userId(source.getUserId())
                .build();
    }

    public static Optional<CreateUserRequestDTO> map(final String source) {
        try {
            return ofNullable(objectMapper.readValue(source, CreateUserRequestDTO.class));
        } catch (JsonProcessingException e) {
            log.info("Failed to parse json", e);
            return empty();
        }
    }

    public static Optional<String> map(final UserDTO source) {
        try {
            return ofNullable(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            log.info("Failed to write json", e);
            return empty();
        }
    }
}
