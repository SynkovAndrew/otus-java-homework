package com.otus.java.coursework.executor;

import java.util.Optional;

public interface ServerRequestExecutor {
    void acceptRequest(int clientId, Object object);

    Optional<Object> getResponse(final int clientId);

    void removeResponse(int clientId);
}
