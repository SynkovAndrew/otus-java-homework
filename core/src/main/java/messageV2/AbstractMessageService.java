package messageV2;

import dto.ParentDTO;
import instance.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import socket.MessageProcessor;
import socket.MessageProcessorType;
import socket.SocketMessageProcessor;
import utils.InstanceInfoUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static messageV2.Queue.INPUT_QUEUE;
import static messageV2.Queue.OUTPUT_QUEUE;

@Slf4j
public abstract class AbstractMessageService implements Instance {
    private static final int THREAD_COUNT = 4;
    private final ExecutorService executorService;
    private final Map<Queue, ArrayBlockingQueue<Message<? extends ParentDTO>>> queues;
    private final List<Runnable> tasks;
    private final MessageProcessorType type;
    protected Socket socket;
    @Value("${socket.server.host}")
    private String host;
    private MessageProcessor messageProcessor;
    @Value("${socket.server.port}")
    private int port;

    public AbstractMessageService(final MessageProcessorType type) {
        this.type = type;
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.queues = Stream.of(Queue.values())
                .collect(toMap(identity(), queue -> new ArrayBlockingQueue<>(10)));
        this.tasks = List.of(
                () -> processQueue(OUTPUT_QUEUE, this::handleOutputQueueMessage),
                this::pollFromMessageProcessor,
                this::putToMessageProcessor
        );
    }

    @PreDestroy
    void destroy() throws IOException {
        socket.close();
        messageProcessor.close();
        executorService.shutdown();
    }

    @Override
    public String getInstanceId() {
        return InstanceInfoUtils.generateInstanceId(host, port, type);
    }

    protected abstract void handleOutputQueueMessage(final Message<? extends ParentDTO> message);

    @PostConstruct
    void init() throws InterruptedException {
        while (isNull(this.socket)) {
            Thread.sleep(3000);
            log.info("Trying to connect to {}...", host + ":" + port);
            try {
                this.socket = new Socket(host, port);
            } catch (Exception ignored) {
            }
        }
        this.messageProcessor = new SocketMessageProcessor(socket, type);
        tasks.forEach(executorService::execute);
    }

    private void pollFromMessageProcessor() {
        while (!executorService.isShutdown()) {
            ofNullable(messageProcessor.pool())
                    .ifPresent(message -> queues.get(OUTPUT_QUEUE).add(message));
        }
    }

    private void processQueue(final Queue queue, final Consumer<Message<? extends ParentDTO>> consumer) {
        while (!Thread.currentThread().isInterrupted()) {
            ofNullable(queues.get(queue).poll())
                    .ifPresent(consumer::accept);
        }
    }

    protected void putInInputQueue(final Message<? extends ParentDTO> message) {
        queues.get(INPUT_QUEUE).add(message);
    }

    private void putToMessageProcessor() {
        while (!executorService.isShutdown()) {
            ofNullable(queues.get(INPUT_QUEUE).poll())
                    .ifPresent(message -> messageProcessor.put(message));
        }
    }
}
