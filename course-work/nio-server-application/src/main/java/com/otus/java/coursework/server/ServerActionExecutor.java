package com.otus.java.coursework.server;

import java.util.Optional;

public interface ServerActionExecutor {
    void acceptJsonMessage(int clientId, String json);

    Optional<String> getJsonResponse(final int clientId);
}
