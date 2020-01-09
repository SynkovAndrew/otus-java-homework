package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.StringMessage;

import java.util.Optional;

public interface Serializer {
    <T> Optional<T> readObject(byte[] bytes, Class<T> clazz);

    Optional<StringMessage> readObject(byte[] bytes);

    Optional<byte[]> writeObject(Object object);

    Optional<byte[]> writeObject(StringMessage object);

}
