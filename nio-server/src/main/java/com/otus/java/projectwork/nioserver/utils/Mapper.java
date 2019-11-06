package com.otus.java.projectwork.nioserver.utils;

import com.otus.java.projectwork.nioserver.domain.User;
import com.otus.java.projectwork.nioserver.dto.CreateUserRequestDTO;
import com.otus.java.projectwork.nioserver.dto.UserDTO;

public class Mapper {
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
