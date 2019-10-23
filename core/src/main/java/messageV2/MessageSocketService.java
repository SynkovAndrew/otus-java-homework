package messageV2;

import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static messageV2.MessageMappingService.mapToJson;
import static messageV2.MessageMappingService.mapToObject;

@Slf4j
public class MessageSocketService {
    private static final String END_OF_MESSAGE = "EOM";

    public static void receiveMessage(final Socket socket, final BlockingQueue<Message<? extends ParentDTO>> queue) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while (nonNull(inputLine = reader.readLine())) {
                if (END_OF_MESSAGE.equals(inputLine)) {
                    final String json = stringBuilder.toString();
                    final Message<? extends ParentDTO> message = mapToObject(json);
                    queue.add(message);
                }
                stringBuilder.append(inputLine);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void sendMessage(final Socket socket, final BlockingQueue<Message<? extends ParentDTO>> queue) {
        try (final PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            while (socket.isConnected()) {
                ofNullable(queue.poll())
                        .ifPresent(message -> {
                            final String json = mapToJson(message);
                            out.println(json);
                            out.println(END_OF_MESSAGE);
                        });
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
