package com.otus.java.multiprocessing.frontendclient.service;

import dto.CreateUserRequestDTO;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class FrontEndService /*extends MessageClient<FindUsersResponseDTO>*/ {
    private static Logger logger = getLogger(FrontEndService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ExecutorService receiver;
    private final ExecutorService sender;
    private final BlockingQueue<CreateUserRequestDTO> queue;

    public FrontEndService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.receiver = newSingleThreadExecutor();
        this.sender = newSingleThreadExecutor();
        this.queue = new ArrayBlockingQueue<>(10);
    }

    @MessageMapping("/user")
    public void handleCreateUser(final CreateUserRequestDTO request) throws InterruptedException {
        //     final var message = messageSystem.createDatabaseMessage(request);
        //   messageSystem.sendMessage(message);

        queue.add(request);


/*        messagingTemplate.convertAndSend("/topic/users",
                List.of(
                        UserDTO.builder()
                                .age(23)
                                .name("Aaa")
                                .build()
                )
        );*/
    }

    @PostConstruct
    public void init() {
        this.sender
    }

    private void processServiceQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final var take = queue.take();
                logger.info("Processing queue message: {}", take);
/*                client.accept(message);*/
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /*  @Override*/
    public void accept(/*final Message<FindUsersResponseDTO> message*/) throws InterruptedException {
/*
        messagingTemplate.convertAndSend("/topic/users", message.getContent().getContent());*/
    }
}
