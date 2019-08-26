package com.otus.java.ioc.messaging.message;

import java.util.stream.Stream;

public enum Queue {
    INBOX_QUEUE("INBOX_QUEUE"),
    FRONT_END_QUEUE("FRONT_END_QUEUE"),
    DATABASE_QUEUE("DATABASE_QUEUE");

    private final String code;

    Queue(final String code) {
        this.code = code;
    }

    public static Queue findByCode(final String code) {
        return Stream.of(Queue.values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public String getCode() {
        return code;
    }
}
