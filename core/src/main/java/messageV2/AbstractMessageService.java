package messageV2;

import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import socket.MessageProcessor;
import socket.MessageProcessorType;
import socket.SocketMessageProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static messageV2.Queue.INPUT_QUEUE;
import static messageV2.Queue.OUTPUT_QUEUE;

@Slf4j
public abstract class AbstractMessageService {
    protected final Map<Queue, ArrayBlockingQueue<Message<? extends ParentDTO>>> queues;
    private final Map<Queue, Runnable> tasks;
    private final Map<Queue, ExecutorService> queueExecutors;
    private final ExecutorService messageExecutor;
    private final MessageProcessorType type;
    protected Socket socket;
    private MessageProcessor messageProcessor;


    @Value("${socket.server.host}")
    private String host;
    @Value("${socket.server.port}")
    private int port;

    public AbstractMessageService(final MessageProcessorType type) {
        this.type = type;
        this.messageExecutor = newSingleThreadExecutor();
        this.queues = Stream.of(Queue.values())
                .collect(toMap(identity(), queue -> new ArrayBlockingQueue<>(10)));
        this.queueExecutors = Stream.of(Queue.values())
                .collect(toMap(identity(), queue -> newSingleThreadExecutor()));
        this.tasks = Map.of(
                INPUT_QUEUE, () -> processQueue(INPUT_QUEUE, this::handleInputQueueMessage),
                OUTPUT_QUEUE, () -> processQueue(OUTPUT_QUEUE, this::handleOutputQueueMessage)
        );
    }

    @PostConstruct
    void init() throws IOException {
        this.socket = new Socket(host, port);
        this.messageProcessor = new SocketMessageProcessor(socket, type);
        messageExecutor.execute(() -> {
            while (!messageExecutor.isShutdown()) {
                ofNullable(messageProcessor.pool())
                        .ifPresent(message -> queues.get(INPUT_QUEUE).add(message));
            }
        });
        queueExecutors.forEach((queue, executor) -> executor.execute(tasks.get(queue)));
    }

    @PreDestroy
    void destroy() throws IOException {
        socket.close();
        messageProcessor.close();
        messageExecutor.shutdown();
        queueExecutors.forEach((queue, executor) -> executor.shutdown());
    }

    private void processQueue(final Queue queue, final Consumer<Message<? extends ParentDTO>> consumer) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Message<? extends ParentDTO> message = queues.get(queue).take();
                log.info("Processing input queue message: {}", message);
                consumer.accept(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected abstract void handleInputQueueMessage(final Message<? extends ParentDTO> message);

    protected abstract void handleOutputQueueMessage(final Message<? extends ParentDTO> message);
}
