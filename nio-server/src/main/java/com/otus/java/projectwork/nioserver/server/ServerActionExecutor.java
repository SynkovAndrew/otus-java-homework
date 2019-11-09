package com.otus.java.projectwork.nioserver.server;

import com.otus.java.projectwork.nioserver.dto.CreateUserRequestDTO;
import com.otus.java.projectwork.nioserver.dto.UserDTO;
import com.otus.java.projectwork.nioserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerActionExecutor {
    private final Map<Integer, UserDTO> responses = new ConcurrentHashMap<>();
    private final UserService userService;

    public void acceptCreateUserRequest(final int id, final CreateUserRequestDTO request) {
        userService.create(request).subscribe(user -> responses.put(id, user));
    }

    public Optional<UserDTO> getResponse(final int id) {
        return Optional.ofNullable(responses.get(id));
    }
}
