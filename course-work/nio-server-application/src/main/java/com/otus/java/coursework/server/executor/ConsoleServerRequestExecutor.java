package com.otus.java.coursework.server.executor;

import com.otus.java.coursework.dto.BaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.otus.java.coursework.utils.Mapper.map;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "console")
public class ConsoleServerRequestExecutor extends AbstractServerRequestExecutor implements ServerRequestExecutor {
    public ConsoleServerRequestExecutor(
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize) {
        super(threadPoolSize);
    }

    @Override
    public void acceptRequest(final int clientId, final BaseDTO dto) {
        executeRequest(clientId, () -> {
            map(dto).ifPresent(json -> log.info("Data {} from client {} has been received", json, clientId));
            return dto;
        });
    }
}
