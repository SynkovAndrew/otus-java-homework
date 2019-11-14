package com.otus.java.coursework.server.executor;

import com.otus.java.coursework.server.ServerActionExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "console")
@RequiredArgsConstructor
public class ConsoleServerActionExecutor extends AbstractServerActionExecutor implements ServerActionExecutor {
    @Override
    public void acceptJsonMessage(final int clientId, final String json) {
        runAction(clientId, () -> {
            log.info("Message {} from client {} has been received", json, clientId);
            return json;
        });
        responses.put(clientId, json);
    }

    @Override
    public Optional<String> getJsonResponse(int clientId) {
        return Optional.ofNullable(responses.get(clientId));
    }
}
