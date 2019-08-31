package com.otus.java.ioc.messaging.system;

import com.otus.java.ioc.messaging.message.MessageClient;
import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import message.Message;
import message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static message.MessageUtils.*;

@Controller
public class FrontEndService extends MessageClient<FindUsersResponseDTO> {
    private static Logger logger = LoggerFactory.getLogger(FrontEndService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public FrontEndService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/user")
    public void handleCreateUser(final CreateUserRequestDTO request) throws InterruptedException {
        logger.info("Handling request: {}", request);
        final var message = createDatabaseMessage(request);
        messageSystem.sendMessage(message);
    }

    @Override
    public void accept(final Message<FindUsersResponseDTO> message) throws InterruptedException {
        logger.info("Message's been received: {}", message);
        messagingTemplate.convertAndSend("/topic/users", message.getContent().getContent());
    }
}
