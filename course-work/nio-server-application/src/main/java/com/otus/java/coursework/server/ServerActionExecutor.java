package com.otus.java.coursework.server;

import com.otus.java.coursework.service.UserService;
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
    private final Map<Integer, String> responses = new ConcurrentHashMap<>();
    private final UserService userService;

    public void acceptJsonMessage(final int clientId, final String json) {
        log.info("Message {} from client {} has been received", json, clientId);
        responses.put(clientId, json);
    }

    public Optional<String> getResponse(final int id) {
        return Optional.ofNullable(responses.get(id));
    }
}
