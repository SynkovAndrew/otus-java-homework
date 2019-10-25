package socket;

import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.Message;
import utils.InstanceInfoUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static messageV2.MessageSocketService.receiveMessage;
import static messageV2.MessageSocketService.sendMessage;

@Slf4j
public class SocketMessageProcessor implements MessageProcessor {
    private static final int THREAD_COUNT = 2;
    private final ExecutorService executorService;
    private final BlockingQueue<Message<? extends ParentDTO>> inputQueue;
    private final BlockingQueue<Message<? extends ParentDTO>> outputQueue;
    private final Socket socket;
    private final MessageProcessorType type;

    public SocketMessageProcessor(final Socket socket, final MessageProcessorType type) {
        this.socket = socket;
        this.type = type;
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.inputQueue = new ArrayBlockingQueue<>(10);
        this.outputQueue = new ArrayBlockingQueue<>(10);
        this.executorService.execute(() -> receiveMessage(socket, outputQueue));
        this.executorService.execute(() -> sendMessage(socket, inputQueue));
        log.info("Socket message processor {}'s been started", type);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }

    @Override
    public String getInstanceId() {
        return InstanceInfoUtils.generateInstanceId(socket.getInetAddress().getHostName(), socket.getPort(), type);
    }

    @Override
    public MessageProcessorType getType() {
        return type;
    }

    @Override
    public Message<? extends ParentDTO> pool() {
        return outputQueue.poll();
    }

    @Override
    public void put(final Message<? extends ParentDTO> message) {
        log.info("Message {}'s been put to message processor {}", message, type);
        inputQueue.add(message);
    }
}
