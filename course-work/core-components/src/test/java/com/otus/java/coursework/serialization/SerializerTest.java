package com.otus.java.coursework.serialization;

import com.otus.java.coursework.dto.StringMessage;
import com.otus.java.coursework.utils.ByteArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializerTest {
    private static Serializer serializer;

    @BeforeAll
    public static void setUp() {
        serializer = new SerializerImpl();
    }

    @Test
    public void serializeDeserializeMessage() {
        final StringMessage message = new StringMessage("First");
        final byte[] messageBytes = serializer.writeObject(message).get();
        final StringMessage firstResult = (StringMessage) serializer.readObject(messageBytes).get();
        assertThat(firstResult).isNotNull();
        assertThat(firstResult.getContent()).isEqualTo("First");
    }

    @Test
    public void serializeDeserializeMessagePartially() {
        final StringMessage message = new StringMessage("This is really long message. " +
                "Much more longer then you have ever read");
        final byte[] messageBytes = serializer.writeObject(message).get();
        final List<byte[]> parts = ByteArrayUtils.parts(messageBytes, 4);
        final byte[] bytes = ByteArrayUtils.flatMap(parts);
        final StringMessage result = (StringMessage) serializer.readObject(bytes).get();
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("This is really long message. " +
                "Much more longer then you have ever read");
    }
}
