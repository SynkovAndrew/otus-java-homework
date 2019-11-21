package com.otus.java.coursework.server.exchange;

public interface ExchangeComponent {
    void putInInbox(int clientId, String json);

    void putInOutbox(int clientId, String json);

    String getFromInbox(int clientId);

    String getFromOutbox(int clientId);
}
