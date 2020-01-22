package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.ByteMessage;
import com.otus.java.coursework.serialization.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "console")
public class ConsoleServerRequestExecutor extends AbstractServerRequestExecutor implements ServerRequestExecutor {
    public ConsoleServerRequestExecutor(
            final @Value("${server.action.executor.thread.pool.size:10}") int threadPoolSize,
            final Serializer serializer) {
        super(threadPoolSize, serializer);
    }

    @Override
    public void acceptRequest(final int clientId, final ByteMessage message) {
        executeRequest(clientId, () -> {
            serializer.readObject(message.getContent(), Object.class)
                    .ifPresent(content -> log.info("Data \"{}\" from client {} has been received", content, clientId));
            return message;
        });
    }
}
