package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.SerializableObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Slf4j
public class SerializerImpl implements Serializer {
    @Override
    public Optional<byte[]> writeObject(final Object object, final Class clazz) {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        try (final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            final byte[] content = byteArrayOutputStream.toByteArray();

            final var innerByteArrayOutputStream = new ByteArrayOutputStream();
            try (final var innerObjectOutputStream = new ObjectOutputStream(innerByteArrayOutputStream)) {
                final var serializableObject = new SerializableObject(content, clazz);
                innerObjectOutputStream.writeObject(serializableObject);
                innerObjectOutputStream.flush();
                final byte[] bytes = innerByteArrayOutputStream.toByteArray();
                return ofNullable(bytes);
            }
        } catch (IOException e) {
            log.info("Failed to serialize object: {}", object);
            return empty();
        }
    }

    @Override
    public  Optional<Object> readObject(final byte[] bytes) {
        final var byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (final var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            final SerializableObject serializableObject = (SerializableObject) objectInputStream.readObject();
            final Class clazz = serializableObject.getClazz();
            final byte[] content = serializableObject.getContent();

            final var innerByteArrayInputStream = new ByteArrayInputStream(content);
            try (final var innerObjectInputStream = new ObjectInputStream(innerByteArrayInputStream)) {
                final Object o = innerObjectInputStream.readObject();
                return ofNullable(o);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.info("Failed to deserialize object");
            return empty();
        }
    }
}
