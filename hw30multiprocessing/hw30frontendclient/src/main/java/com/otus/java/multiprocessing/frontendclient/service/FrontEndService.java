package com.otus.java.multiprocessing.frontendclient.service;

import dto.CreateUserRequestDTO;
import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.AbstractMessageService;
import messageV2.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static messageV2.MessageSocketService.sendMessage;
import static messageV2.Queue.OUTPUT_QUEUE;
import static socket.MessageProcessorType.FRONTEND;


@Slf4j
@Controller
public class FrontEndService extends AbstractMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    public FrontEndService(final SimpMessagingTemplate messagingTemplate) {
        super(FRONTEND);
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/user")
    public void handleCreateUser(final CreateUserRequestDTO request) {
        final Message<CreateUserRequestDTO> message = new Message<>(request, request.getClass().getSimpleName());
        queues.get(OUTPUT_QUEUE).add(message);
        log.info("Message's been put to input queue: {}", message);
    }

    @Override
    public void handleOutputQueueMessage(final Message<? extends ParentDTO> message) {
        sendMessage(socket, message);
        log.info("Message's been sent to message server: {}", message);
    }

    @Override
    public void handleInputQueueMessage(final Message<? extends ParentDTO> message) {
        messagingTemplate.convertAndSend("/topic/users", message.getContent());
        log.info("Message's been sent to front end: {}", message);
    }
}

