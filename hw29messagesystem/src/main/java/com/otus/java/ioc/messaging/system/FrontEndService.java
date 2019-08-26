package com.otus.java.ioc.messaging.system;

import com.otus.java.ioc.dto.FindUsersResponseDTO;
import com.otus.java.ioc.messaging.message.Message;
import com.otus.java.ioc.messaging.message.MessageClient;
import com.otus.java.ioc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.otus.java.ioc.configuration.Constant.FRONT_END_SERVICE_QUALIFIER;

@Service
@Qualifier(value = FRONT_END_SERVICE_QUALIFIER)
public class FrontEndService extends MessageClient<FindUsersResponseDTO> {
    private static Logger logger = LoggerFactory.getLogger(FrontEndService.class);
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FrontEndService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void accept(final Message<FindUsersResponseDTO> message) throws InterruptedException {
        logger.info("Message's been received: {}", message);
        final var content = message.getContent();
        messagingTemplate.convertAndSend("/topic/users", content.getContent());
    }
}
