package com.otus.multiprocessing.messageserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.otus.multiprocessing.messageserver.utils.ProcessRunnerUtils.*;
import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class MessageServerApplication {
    public static void main(String[] args) throws IOException {
        run(MessageServerApplication.class, args);

        final ProcessBuilder processBuilder1 = new ProcessBuilder(getCommand(DATABASE_CLIENT_PORTS.get(0), PATH_TO_DATABASE_CLIENT));
/*        final ProcessBuilder processBuilder2 = new ProcessBuilder(ProcessRunnerUtils.getCommand(9001, PATH_TO_FRONTEND_CLIENT));
        final ProcessBuilder processBuilder3 = new ProcessBuilder(ProcessRunnerUtils.getCommand(9010, PATH_TO_DATABASE_CLIENT));
        final ProcessBuilder processBuilder4 = new ProcessBuilder(ProcessRunnerUtils.getCommand(9011, PATH_TO_DATABASE_CLIENT));*/
        final Process process = processBuilder1.start();


        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(">>>>>>>>> " + line);
            }

        } finally {
            process.destroy();
        }
    }
}
