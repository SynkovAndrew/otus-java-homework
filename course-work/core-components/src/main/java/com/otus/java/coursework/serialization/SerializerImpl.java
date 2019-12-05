package com.otus.java.coursework.serialization;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Optional;

import static java.util.Optional.*;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Slf4j
public class SerializerImpl implements Serializer {
    @Override
    public Optional<byte[]> writeObject(final Object object) {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        try (final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            return of(bytes);
        } catch (IOException e) {
            log.info("Failed to serialize object: {}", object);
            return empty();
        }
    }

    @Override
    public <T> Optional<T> readObject(final byte[] bytes, final Class<T> clazz) {
        final var byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (final var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            final Object object = objectInputStream.readObject();
            return ofNullable(clazz.cast(object));
        } catch (IOException | ClassNotFoundException e) {
            log.info("Failed to deserialize object");
            return empty();
        }
    }
}
