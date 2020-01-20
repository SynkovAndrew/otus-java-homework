package com.otus.java.coursework.service;

import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;

import java.util.List;

public interface UserDBService {
    UserDTO create(final CreateUserRequestDTO request);

    List<UserDTO> findAll();
}
