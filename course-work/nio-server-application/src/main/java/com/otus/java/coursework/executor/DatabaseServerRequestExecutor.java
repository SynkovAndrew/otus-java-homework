package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.StringMessage;
import com.otus.java.coursework.service.UserDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

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
    public void acceptRequest(final int clientId, final StringMessage dto) {
      /*  if (dto instanceof CreateUserRequestDTO) {
            executeRequest(clientId, () -> {
                log.info("Processing request {} from client {}...", dto, clientId);
                return dbService.create((CreateUserRequestDTO) dto);
            });
        }*/
    }
}
