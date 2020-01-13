package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.StringMessage;

import java.util.Optional;

public interface Serializer {
    Optional<StringMessage> readObject(byte[] bytes);

    Optional<byte[]> writeObject(StringMessage object);
}
