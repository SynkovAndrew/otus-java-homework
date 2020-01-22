package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.ByteMessage;

import java.util.Optional;

public interface ServerRequestExecutor {
    void acceptRequest(int clientId, ByteMessage object);

    Optional<ByteMessage> getResponse(final int clientId);

    void removeResponse(int clientId);
}
