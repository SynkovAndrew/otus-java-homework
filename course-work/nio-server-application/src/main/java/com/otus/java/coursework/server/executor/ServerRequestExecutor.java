package com.otus.java.coursework.server.executor;

import com.otus.java.coursework.dto.BaseDTO;

import java.util.Optional;

public interface ServerRequestExecutor {
    void acceptRequest(int clientId, BaseDTO json);

    Optional<BaseDTO> getResponse(final int clientId);
}
