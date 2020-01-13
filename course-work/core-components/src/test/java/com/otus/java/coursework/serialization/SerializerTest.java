package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.StringMessage;
import com.otus.java.coursework.utils.ByteArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializerTest {
    private static Serializer serializer;

    @BeforeAll
    public static void setUp() {
        serializer = new SerializerImpl();
    }

    @Test
    public void serializeDeserializeMessage() {
        final StringMessage firstMessage = new StringMessage("First");

        final byte[] firstMessageBytes = serializer.writeObject(firstMessage).get();
        final byte[] joinedBytes = new byte[firstMessageBytes.length];

        ByteArrayUtils.fill(joinedBytes, firstMessageBytes);

        final StringMessage firstResult = serializer.readObject(joinedBytes).get();

        assertThat(firstResult).isNotNull();
        assertThat(firstResult.getContent()).isEqualTo("First");
    }
}
