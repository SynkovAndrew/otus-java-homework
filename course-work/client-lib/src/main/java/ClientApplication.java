import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CreateUserRequestDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ClientApplication {
    public static void main(String[] args) {
        final ExecutorService executorService = newFixedThreadPool(2);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final Socket socket = new Socket("localhost", 4455);
            try (final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                System.out.println("Sending message to server...");
                final CreateUserRequestDTO request = CreateUserRequestDTO.builder().age(11).name("Pavel").build();
                final String json = mapper.writeValueAsString(request);
                writer.write(json);
                writer.write('\n');
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
