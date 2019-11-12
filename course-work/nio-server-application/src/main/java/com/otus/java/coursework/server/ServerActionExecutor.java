package com.otus.java.coursework.server;

import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.otus.java.coursework.service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerActionExecutor {
    private final Map<Integer, UserDTO> responses = new ConcurrentHashMap<>();
    private final UserService userService;

    public void acceptCreateUserRequest(final int clientId, final CreateUserRequestDTO request) {
        userService.create(request).subscribe(user -> responses.put(clientId, user));
    }

    public Optional<UserDTO> getResponse(final int id) {
        return Optional.ofNullable(responses.get(id));
    }
}
