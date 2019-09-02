package com.otus.multiprocessing.messageserver.socket;

import com.otus.multiprocessing.messageserver.service.MessageMappingService;
import dto.ParentDTO;
import messageV2.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class SocketMessageProcessor implements MessageProcessor {
    private static final String END_OF_MESSAGE = "EOM";
    private static final int THREAD_COUNT = 2;

    private final ExecutorService executorService;
    private final Socket socket;

    private final BlockingQueue<Message<? extends ParentDTO>> outputQueue;
    private final BlockingQueue<Message<? extends ParentDTO>> inputQueue;

    public SocketMessageProcessor(final Socket socket) {
        this.socket = socket;
        this.executorService = newFixedThreadPool(THREAD_COUNT);
        this.inputQueue = new ArrayBlockingQueue<>(10);
        this.outputQueue = new ArrayBlockingQueue<>(10);
    }

    @Override
    public Message<? extends ParentDTO> pool() {
        return null;
    }

    @Override
    public void send(Message<? extends ParentDTO> message) {

    }

    @Override
    public Message<? extends ParentDTO> take() {
        return null;
    }

    @Override
    public void close() {

    }

    private void receiveMessage() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            final StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while (nonNull(inputLine = reader.readLine())) {
                if (END_OF_MESSAGE.equals(inputLine)) {
                    final String json = stringBuilder.toString();
                    final var o = MessageMappingService.mapToObject(json);

                    Message message = getMessageFromGson(json);
                    input.add(message);
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(inputLine);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Message getMessageFromGson(String json) throws ClassNotFoundException {

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject) parser.parse(json);
        //String className = String.valueOf(jsonObject.get(Message.CLASS_NAME_VARIABLE));
        Class<?> messageClass = PingMessage.class;

        return (Message) new Gson().fromJson(json, messageClass);
    }

}
