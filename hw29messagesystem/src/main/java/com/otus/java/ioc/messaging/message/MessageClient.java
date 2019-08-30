package com.otus.java.ioc.messaging.message;

import com.otus.java.ioc.messaging.system.MessageSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public abstract class MessageClient<T> {
    protected MessageSystem messageSystem;

    @Autowired
    public void setMessageSystem(MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
    }

    public abstract void accept(final Message<T> message) throws InterruptedException;
}
