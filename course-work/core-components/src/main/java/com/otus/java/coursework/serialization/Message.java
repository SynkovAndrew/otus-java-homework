package com.otus.java.coursework.serialization;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Message<T> {
    private final String clazz;
    private final T content;

    public Message(final T content) {
        this.content = content;
        this.clazz = content.getClass().getSimpleName();
    }
}
