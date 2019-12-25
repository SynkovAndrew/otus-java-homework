package com.otus.java.coursework.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "console")
public class ConsoleServerRequestExecutor extends AbstractServerRequestExecutor implements ServerRequestExecutor {
    public ConsoleServerRequestExecutor(
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize) {
        super(threadPoolSize);
    }

    @Override
    public void acceptRequest(final int clientId, final Object object) {
        executeRequest(clientId, () -> {
            log.info("\nData {} from client {} has been received\n", object, clientId);
            return object;
        });
    }
}
