package com.otus.java.coursework;

import com.otus.java.coursework.client.Client;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) {
        try (final var client = new Client("localhost", 4455)) {
            for (int i = 1; i < 100; i++) {
                client.send(CreateUserRequestDTO.builder()
                        .age(i)
                        .name("Human " + i)
                        .build());
            }
        } catch (FailedToCreateClientException e) {
            log.error("Failed o create client", e);
        }
    }
}
