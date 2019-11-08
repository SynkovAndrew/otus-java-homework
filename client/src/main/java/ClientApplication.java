import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CreateUserRequestDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ClientApplication {
    public static void main(String[] args) {
        final ExecutorService executorService = newFixedThreadPool(2);
        final ObjectMapper mapper = new ObjectMapper();
        try (final Socket socket = new Socket("localhost", 4455)) {
            executorService.execute(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        stringBuilder.append(inputLine);
                    }
                    System.out.println("Response from server: " + stringBuilder.toString());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            executorService.execute(() -> {
                try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    final CreateUserRequestDTO request = CreateUserRequestDTO.builder().age(11).name("Pavel").build();
                    final String json = mapper.writeValueAsString(request);
                    writer.write(json);
                    writer.write('\n');
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
