package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.ByteMessage;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.serialization.Serializer;
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
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize,
            final Serializer serializer) {
        super(threadPoolSize, serializer);
        this.dbService = dbService;
    }

    @Override
    public void acceptRequest(final int clientId, final Object object) {
        executeRequest(clientId, () -> {
            serializer.readObject(((ByteMessage) object).getContent())
                    .ifPresent(content -> {
                        if (content instanceof CreateUserRequestDTO) {
                            log.info("Processing request {} from client {}...", content, clientId);
                            dbService.create(content);
                        }
                    });
            return object;
        });
    }
}
