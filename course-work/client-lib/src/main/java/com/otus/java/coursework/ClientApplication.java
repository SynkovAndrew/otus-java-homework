package com.otus.java.coursework;

import com.otus.java.coursework.client.Client;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.SerializerImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) {
        final ExecutorService executorService = newFixedThreadPool(100);
        for (int clientNumber = 0; clientNumber < 100; clientNumber++) {
            executorService.submit(() -> {
                try (final var client = new Client("localhost", 4455, new SerializerImpl())) {
                    for (int i = 1; i < 5; i++) {
                        final CreateUserRequestDTO request = CreateUserRequestDTO.builder()
                                .age(i)
                                .firstName("aaa")
                                .lastName("bbb")
                                .build();
                        client.send(request);
                    }
                } catch (FailedToCreateClientException | IOException e) {
                    log.error("Failed o create client", e);
                } /*catch (InterruptedException ignored) {
                }*/
            });
        }
    }
}
