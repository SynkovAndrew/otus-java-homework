package com.otus.java.multiprocessing.frontendclient.service;

import dto.ParentDTO;
import dto.CreateUserRequestDTO;
import messageV2.AbstractMessageService;
import messageV2.Message;
import messageV2.Queue;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;

import static messageV2.Queue.INPUT_QUEUE;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class FrontEndService extends AbstractMessageService {
    private static Logger logger = getLogger(FrontEndService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public FrontEndService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/user")
    public void handleCreateUser(final CreateUserRequestDTO request) {
        final var message = new Message<>(request);
        queues.get(INPUT_QUEUE).add(message);
        logger.info("Message's been put to input queue: {}", message);
    }

    @PostConstruct
    void init() {
        executors.forEach((queue, executor) -> executor.execute(tasks.get(queue)));
    }

    @PreDestroy
    void destroy() {
        executors.forEach((queue, executor) -> executor.shutdown());
    }

    @Override
    protected void processQueue(final Queue queue, final Consumer<Message<? extends ParentDTO>> consumer) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Message<? extends ParentDTO> message = queues.get(queue).take();
                logger.info("Processing input queue message: {}", message);
                consumer.accept(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void handleOutputQueueMessage(final Message<? extends ParentDTO> message) {
        messagingTemplate.convertAndSend("/topic/users", message.getContent());
        logger.info("Message's been sent to front end: {}", message);
    }

    @Override
    public void handleInputQueueMessage(final Message<? extends ParentDTO> message) {
        logger.info("Message's been sent to message system application: {}", message);
    }
}
