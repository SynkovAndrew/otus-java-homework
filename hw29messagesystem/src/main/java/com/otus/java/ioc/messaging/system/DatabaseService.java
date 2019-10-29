package com.otus.java.ioc.messaging.system;

import com.otus.java.ioc.messaging.message.MessageClient;
import com.otus.java.ioc.service.UserService;
import dto.CreateUserRequestDTO;
import message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static message.MessageUtils.createFrontEndMessage;
import static service.MappingService.map;
import static service.MappingService.mapAsResponse;


@Service
public class DatabaseService extends MessageClient<CreateUserRequestDTO> {
    private static Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private final UserService userService;

    public DatabaseService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void accept(final Message<CreateUserRequestDTO> message) throws InterruptedException {
        logger.info("Message's been received: {}", message);
        final var content = message.getContent();
        final var user = map(content);
        userService.create(user);

        final var users = userService.loadAll();
        final var frontEndMessage = createFrontEndMessage(mapAsResponse(users));
        messageSystem.sendMessage(frontEndMessage);
        logger.info("Message's been sent: {}", frontEndMessage);
    }
}
