package com.otus.java.coursework.server.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public abstract class AbstractServerActionExecutor {
    final Map<Integer, String> responses;
    private final ExecutorService executorService;
    @Value("${server.action.executor.thread.pool.size:10}")
    private int threadPoolSize;

    protected AbstractServerActionExecutor() {
        this.responses = new ConcurrentHashMap<>();
        this.executorService = newFixedThreadPool(threadPoolSize);
    }

    void runAction(final int clientId, final Supplier<String> supplier) {
        CompletableFuture.supplyAsync(supplier, executorService)
                .thenApply((result) -> responses.put(clientId, result));
    }
}
