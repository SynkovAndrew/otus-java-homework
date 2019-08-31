package com.otus.java.ioc.messaging.message;

public interface Message<T> {
    Queue getTargetQueue();

    T getContent();
}
