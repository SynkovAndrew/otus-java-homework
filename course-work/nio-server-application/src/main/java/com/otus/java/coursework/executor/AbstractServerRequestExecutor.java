package com.otus.java.coursework.executor;

import com.otus.java.coursework.dto.ByteMessage;
import com.otus.java.coursework.serialization.Serializer;
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
    protected final Serializer serializer;
    private final ExecutorService executorService;
    private final Map<Integer, ByteMessage> responses;

    protected AbstractServerRequestExecutor(final int threadPoolSize, final Serializer serializer) {
        this.responses = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(threadPoolSize);
        this.serializer = serializer;
    }

    void executeRequest(final int clientId, final Supplier<ByteMessage> supplier) {
        supplyAsync(supplier, executorService)
                .thenApply((response) -> responses.put(clientId, response));
    }

    public Optional<ByteMessage> getResponse(final int clientId) {
        return ofNullable(responses.get(clientId));
    }

    public void removeResponse(final int clientId) {
        responses.computeIfPresent(clientId, (key, value) -> responses.remove(clientId));
    }
}
