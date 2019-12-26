package com.otus.java.coursework;

import com.otus.java.coursework.client.Client;
import com.otus.java.coursework.dto.UserDTO;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.SerializerImpl;
import com.otus.java.coursework.service.ByteProcessorServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) {
        try (final var client = new Client("localhost", 4455,
                new SerializerImpl(), new ByteProcessorServiceImpl())) {
            for (int i = 1; i < 100; i++) {
                final var user = UserDTO.builder()
                        .age(33)
                        .userId(i)
                        .firstName("Test")
                        .lastName("Test")
                        .build();
                client.send(user);
                Thread.sleep(3000);
            }
        } catch (FailedToCreateClientException e) {
            log.error("Failed o create client", e);
        } catch (InterruptedException ignored) {
        }
    }
}
