package com.otus.java.ioc.messaging.system;


import com.otus.java.ioc.messaging.message.*;
import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static com.otus.java.ioc.messaging.message.Queue.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class MessageSystem {
    private static Logger logger = getLogger(MessageSystem.class);
    private final Map<Queue, ArrayBlockingQueue<Message>> queues;
    private final Map<Queue, ExecutorService> executors;
    private final Map<Queue, Runnable> tasks;

    private FrontEndService frontEndService;
    private MessageClient databaseService;

    public MessageSystem() {
        this.queues = Stream.of(Queue.values())
                .collect(toMap(identity(), v -> new ArrayBlockingQueue<>(10)));
        this.executors = Stream.of(Queue.values())
                .collect(toMap(identity(), v -> newSingleThreadExecutor()));
        this.tasks = Map.of(
                INBOX_QUEUE, this::processInboxQueue,
                FRONT_END_QUEUE, () -> processServiceQueue(queues.get(FRONT_END_QUEUE), frontEndService),
                DATABASE_QUEUE, () -> processServiceQueue(queues.get(DATABASE_QUEUE), databaseService)
        );
    }

    @PostConstruct
    void init() {
        executors.forEach((queue, executor) -> executor.execute(tasks.get(queue)));
        executors.forEach((queue, executor) -> executor.shutdown());
    }

    public void sendMessage(final Message message) throws InterruptedException {
        queues.get(INBOX_QUEUE).put(message);
    }

    private void processInboxQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Message message = queues.get(INBOX_QUEUE).take();
                logger.info("Processing inbox queue message: {}", message);
                queues.get(message.getTargetQueue()).put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processServiceQueue(final ArrayBlockingQueue<Message> queue,
                                     final MessageClient client) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Message message = queue.take();
                logger.info("Processing service queue message: {}", message);
                client.accept(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public CreateUserMessage createDatabaseMessage(final CreateUserRequestDTO content) {
        return CreateUserMessage.builder()
                .content(content)
                .build();
    }

    public FindUsersMessage createFrontEndMessage(final FindUsersResponseDTO content) {
        return FindUsersMessage.builder()
                .content(content)
                .build();
    }

    @Autowired
    public void setFrontEndService(final FrontEndService frontEndService) {
        this.frontEndService = frontEndService;
    }

    @Autowired
    public void setDatabaseService(final DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}
