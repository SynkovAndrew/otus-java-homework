package ru.otus.homework.utils;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.homework.data.Data;

import java.util.Collections;
import java.util.List;

public class SerializationUtilsTest {
    private Gson gson;

    @BeforeEach
    public void setUp() {
        this.gson = new Gson();
    }

    @Test
    public void testToJson() {
        final Data data = new Data();
        final String json = SerializationUtils.toJson(data);

        Assertions.assertEquals(gson.toJson(data), json);
    }

    @Test
    public void testToJson2() {
        Assertions.assertEquals(SerializationUtils.toJson(null), gson.toJson(null));
        Assertions.assertEquals(SerializationUtils.toJson((byte) 1), gson.toJson((byte) 1));
        Assertions.assertEquals(SerializationUtils.toJson((short) 1f), gson.toJson((short) 1f));
        Assertions.assertEquals(SerializationUtils.toJson(1), gson.toJson(1));
        Assertions.assertEquals(SerializationUtils.toJson(1L), gson.toJson(1L));
        Assertions.assertEquals(SerializationUtils.toJson(1f), gson.toJson(1f));
        Assertions.assertEquals(SerializationUtils.toJson(1d), gson.toJson(1d));
        Assertions.assertEquals(SerializationUtils.toJson("aaa"), gson.toJson("aaa"));
        Assertions.assertEquals(SerializationUtils.toJson('a'), gson.toJson('a'));
        Assertions.assertEquals(SerializationUtils.toJson(new int[]{1, 2, 3}), gson.toJson(new int[]{1, 2, 3}));
        Assertions.assertEquals(SerializationUtils.toJson(List.of(1, 2, 3)), gson.toJson(List.of(1, 2, 3)));
        Assertions.assertEquals(SerializationUtils.toJson(Collections.singletonList(1)), gson.toJson(Collections.singletonList(1)));

    }
}
