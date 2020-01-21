package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.*;
import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.service.UserDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

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
        executeRequest(clientId, () -> serializer.readObject(((ByteMessage) object).getContent())
                .map(content -> {
                    log.info("Processing request {} from client {}...", content, clientId);
                    if (content instanceof CreateUserRequestDTO) {
                        final UserDTO user = dbService.create((CreateUserRequestDTO) content);
                        final ByteMessage byteMessage = serializer.writeObject(user)
                                .map(ByteMessage::new)
                                .orElse(null);
                        return nonNull(byteMessage) ? byteMessage : object;
                    } else if (content instanceof FindUsersRequestDTO) {
                        final List<UserDTO> users = dbService.findAll();
                        final FindUsersResponseDTO response = FindUsersResponseDTO.builder()
                                .content(users)
                                .size(users.size())
                                .build();
                        final ByteMessage byteMessage = serializer.writeObject(response)
                                .map(ByteMessage::new)
                                .orElse(null);
                        return nonNull(byteMessage) ? byteMessage : object;
                    }
                    return object;
                })
                .orElse(object));
    }
}
