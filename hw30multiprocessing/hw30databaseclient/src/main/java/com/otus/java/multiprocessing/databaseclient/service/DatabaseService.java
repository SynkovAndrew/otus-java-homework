package com.otus.java.multiprocessing.databaseclient.service;

import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.AbstractMessageService;
import messageV2.Message;
import org.springframework.stereotype.Service;

import static socket.MessageProcessorType.DATABASE;

@Service
@Slf4j
public class DatabaseService extends AbstractMessageService {
    private final UserServiceAdapter userService;

    public DatabaseService(UserServiceAdapter userService) {
        super(DATABASE);
        this.userService = userService;
    }

    @Override
    protected void handleOutputQueueMessage(final Message<? extends ParentDTO> message) {
        // TODO: Think about a way to overcome class casting !!!
        userService.createUser((CreateUserRequestDTO) message.getContent());
        final FindUsersResponseDTO response = userService.findUsers();
        final var responseMessage = new Message<>(
                response.getClass().getSimpleName(), response, message.getInstanceId());
        putInInputQueue(responseMessage);
    }
}
