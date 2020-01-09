package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.StringMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Optional;

import static java.util.Optional.*;

@Slf4j
public class SerializerImpl implements Serializer {
    private Object doReadObject(final byte[] bytes) {
        final var byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (final var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.info("Failed to deserialize object");
            return null;
        }
    }

    private Optional<byte[]> doWriteObject(final Object object) {
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
        return ofNullable(doReadObject(bytes))
                .map(clazz::cast);
    }

    @Override
    public Optional<StringMessage> readObject(final byte[] bytes) {
        return ofNullable(doReadObject(bytes))
                .map(object -> (StringMessage) object);
    }

    @Override
    public Optional<byte[]> writeObject(final StringMessage message) {
        return doWriteObject(message);
    }

    @Override
    public Optional<byte[]> writeObject(final Object object) {
        return doWriteObject(object);
    }
}
