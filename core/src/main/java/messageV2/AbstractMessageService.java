package messageV2;

import dto.ParentDTO;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static messageV2.Queue.INPUT_QUEUE;
import static messageV2.Queue.OUTPUT_QUEUE;

public abstract class AbstractMessageService {
    protected final Map<Queue, ArrayBlockingQueue<Message<? extends ParentDTO>>> queues;
    protected final Map<Queue, Runnable> tasks;
    protected final Map<Queue, ExecutorService> executors;

    public AbstractMessageService() {
        this.queues = Stream.of(Queue.values())
                .collect(toMap(identity(), queue -> new ArrayBlockingQueue<>(10)));
        this.executors = Stream.of(Queue.values())
                .collect(toMap(identity(), queue -> newSingleThreadExecutor()));
        this.tasks = Map.of(
                INPUT_QUEUE, () -> processQueue(INPUT_QUEUE, this::handleInputQueueMessage),
                OUTPUT_QUEUE, () -> processQueue(OUTPUT_QUEUE, this::handleOutputQueueMessage)
        );
    }

    protected abstract void processQueue(final Queue queue, final Consumer<Message<? extends ParentDTO>> consumer);

    protected abstract void handleInputQueueMessage(final Message<? extends ParentDTO> message);

    protected abstract void handleOutputQueueMessage(final Message<? extends ParentDTO> message);
}
