package com.otus.java.projectwork.nioserver.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
public class Subscription<T> {
    private Mono<T> publisher;
    private boolean isSubscribed;
}
