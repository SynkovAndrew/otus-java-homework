package com.otus.java.coursework.server.executor;

import com.otus.java.coursework.server.ServerActionExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "file")
@RequiredArgsConstructor
public class FileServerActionExecutor implements ServerActionExecutor {
    private final Map<Integer, String> responses = new ConcurrentHashMap<>();

    @Override
    public void acceptJsonMessage(final int clientId, final String json) {
        log.info("Message {} from client {} has been received", json, clientId);
        responses.put(clientId, json);
    }

    @Override
    public Optional<String> getJsonResponse(int clientId) {
        return Optional.ofNullable(responses.get(clientId));
    }
}
