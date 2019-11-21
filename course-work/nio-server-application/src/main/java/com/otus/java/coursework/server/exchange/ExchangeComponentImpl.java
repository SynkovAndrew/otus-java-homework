package com.otus.java.coursework.server.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class ExchangeComponentImpl implements ExchangeComponent {
    private final ConcurrentMap<Integer, String> inbox;
    private final ConcurrentMap<Integer, String> outbox;

    public ExchangeComponentImpl(final @Value("${exchange.component.queue.size:100}") int queueSize) {
        this.inbox = new ConcurrentHashMap<>(queueSize);
        this.outbox = new ConcurrentHashMap<>(queueSize);
    }

    @Override
    public void putInInbox(final int clientId, final String json) {
        inbox.put(clientId, json);
    }

    @Override
    public void putInOutbox(final int clientId, final String json) {
        outbox.put(clientId, json);
    }

    @Override
    public String getFromInbox(final int clientId) {
        return inbox.get(clientId);
    }

    @Override
    public String getFromOutbox(final int clientId) {
        return outbox.get(clientId);
    }
}
