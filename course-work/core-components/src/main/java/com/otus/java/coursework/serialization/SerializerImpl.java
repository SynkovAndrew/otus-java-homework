package com.otus.java.coursework.serialization;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Optional;

import static java.util.Optional.*;

@Slf4j
public class SerializerImpl implements Serializer {

    @Override
    public <T> Optional<T> readObject(final byte[] bytes, final Class<T> clazz) {
        try (final var byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            try (final var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                return ofNullable(objectInputStream.readObject())
                        .map(clazz::cast);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.info("Failed to deserialize object");
            return Optional.empty();
        }
    }

    @Override
    public Optional<byte[]> writeObject(final Object object) {
        try (final var byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                final byte[] bytes = byteArrayOutputStream.toByteArray();
                return of(bytes);
            }
        } catch (IOException e) {
            log.info("Failed to serialize object: {}", object);
            return empty();
        }
    }
}
