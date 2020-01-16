package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.StringMessage;

import java.util.Optional;

public interface ServerRequestExecutor {
    void acceptRequest(int clientId, StringMessage object);

    Optional<StringMessage> getResponse(final int clientId);

    void removeResponse(int clientId);
}
