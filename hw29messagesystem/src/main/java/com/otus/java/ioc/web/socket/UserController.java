package com.otus.java.ioc.web.socket;

import com.otus.java.ioc.dto.CreateUserRequestDTO;
import com.otus.java.ioc.messaging.system.MessageSystem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final MessageSystem messageSystem;

    @MessageMapping("/user")
    /*    @SendTo("/topic/messages")*/
    public void handleCreateUser(final CreateUserRequestDTO request) throws InterruptedException {
        logger.info("Handling request: {}", request);
        final var message = messageSystem.createDatabaseMessage(request);
        messageSystem.sendMessage(message);
    }
}

