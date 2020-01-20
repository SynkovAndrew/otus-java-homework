package com.otus.java.coursework.service;

import com.otus.java.coursework.domain.User;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;

public class Mapper {
    public static User map(final CreateUserRequestDTO request) {
        return User.builder()
                .age(request.getAge())
                .name(request.getName())
                .build();
    }

    public static UserDTO map(final User user) {
        return UserDTO.builder()
                .age(user.getAge())
                .name(user.getName())
                .userId(user.getUserId())
                .build();
    }
}
