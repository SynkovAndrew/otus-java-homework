package com.otus.java.coursework.serialization;

import java.util.Optional;

public interface Serializer {
    <T> Optional<T> readObject(byte[] bytes, Class<T> clazz);

    Optional<byte[]> writeObject(Object object);
}
