package com.otus.java.coursework.serialization;

import java.util.Optional;

public interface Serializer {
    Optional<Object> readObject(byte[] bytes);

    Optional<byte[]> writeObject(Object object);
}
