package com.otus.java.coursework.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public abstract class AbstractServerRequestExecutor {
    private final ExecutorService executorService;
    private final Map<Integer, Object> responses;

    protected AbstractServerRequestExecutor(final int threadPoolSize) {
        this.responses = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(threadPoolSize);
    }

    void executeRequest(final int clientId, final Supplier<Object> supplier) {
        supplyAsync(supplier, executorService)
                .thenApply((response) -> responses.put(clientId, response));
    }

    public Optional<Object> getResponse(final int clientId) {
        return ofNullable(responses.get(clientId));
    }
}
