package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.ByteMessage;
import com.otus.java.coursework.dto.StringMessage;

import java.util.Optional;

public interface Serializer {
    Optional<StringMessage> readStringMessage(byte[] bytes);

    Optional<Object> readObject(byte[] bytes);

    Optional<ByteMessage> readByteMessage(byte[] bytes);

    Optional<byte[]> writeStringMessage(StringMessage message);

    Optional<byte[]> writeObject(Object object);

    Optional<byte[]> writeByteMessage(ByteMessage message);
}
