package com.otus.java.coursework;

import com.github.javafaker.Faker;
import com.otus.java.coursework.client.ClientLibrary;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.FindUsersRequestDTO;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.SerializerImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public class ClientApplication {
    private static Faker faker = new Faker();
    public static void main(String[] args) throws InterruptedException {

        // Demo if working with console
/*        try (final var client = new Client("localhost", 4455, new SerializerImpl())) {
            client.send("Hello");
            client.send("world");
            client.send("This");
            client.send("is");
            client.send("Andrew!");
        } catch (FailedToCreateClientException | IOException e) {
            log.error("Failed o create client", e);
        }*/

        // Demo of working with database
        final ExecutorService executorService = newFixedThreadPool(100);
        try (final var client = new ClientLibrary("localhost", 4455, new SerializerImpl())) {
            client.send(FindUsersRequestDTO.builder().build());
        } catch (FailedToCreateClientException | IOException e) {
            log.error("Failed o create client", e);
        }
        Thread.sleep(10000);
        for (int clientNumber = 1; clientNumber <= 100; clientNumber++) {
            executorService.submit(() -> {
                try (final var client = new ClientLibrary("localhost", 4455, new SerializerImpl())) {
                    for (int i = 1; i <= 500; i++) {
                        final CreateUserRequestDTO request = CreateUserRequestDTO.builder()
                                .age(faker.number().numberBetween(10, 70))
                                .name(faker.name().fullName())
                                .build();
                        client.send(request);
                    }
                } catch (FailedToCreateClientException | IOException e) {
                    log.error("Failed o create client", e);
                }
            });
        }
        Thread.sleep(50000);
        try (final var client = new ClientLibrary("localhost", 4455, new SerializerImpl())) {
            client.send(FindUsersRequestDTO.builder().build());
        } catch (FailedToCreateClientException | IOException e) {
            log.error("Failed o create client", e);
        }
    }
}
