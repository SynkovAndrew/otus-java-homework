package com.otus.java.coursework.server.executor;

import com.otus.java.coursework.dto.BaseDTO;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.server.ServerRequestExecutor;
import com.otus.java.coursework.service.UserDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "database")
public class DatabaseServerRequestExecutor extends AbstractServerRequestExecutor implements ServerRequestExecutor {
    private final UserDBService dbService;

    public DatabaseServerRequestExecutor(
            final UserDBService dbService,
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize) {
        super(threadPoolSize);
        this.dbService = dbService;
    }

    @Override
    public void acceptRequest(final int clientId, final BaseDTO dto) {
        executeRequest(clientId, () -> {
            if (dto instanceof CreateUserRequestDTO) {
                log.info("Processing request {} from client {}", dto, clientId);
                return dbService.create((CreateUserRequestDTO) dto).block();
            } else {
                log.info("Request {} from client {} has wrong type", dto, clientId);
                return dto;
            }
        });
    }

    @Override
    public Optional<BaseDTO> getResponse(final int clientId) {
        return getResponse(clientId);
    }
}
