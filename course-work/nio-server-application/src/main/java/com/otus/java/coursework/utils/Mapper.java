package com.otus.java.coursework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.java.coursework.domain.User;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

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
}
