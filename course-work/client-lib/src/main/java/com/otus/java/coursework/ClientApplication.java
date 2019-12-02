package com.otus.java.coursework;

import com.otus.java.coursework.client.Client;
import com.otus.java.coursework.exception.FailedToCreateClientException;
import com.otus.java.coursework.serialization.SerializerImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) {
        try (final var client = new Client("localhost", 4455, new SerializerImpl())) {
            for (int i = 1; i < 100; i++) {
                client.send("Hello " + i);
                Thread.sleep(3000);
            }
        } catch (FailedToCreateClientException e) {
            log.error("Failed o create client", e);
        } catch (InterruptedException ignored) {
        }
    }
}
