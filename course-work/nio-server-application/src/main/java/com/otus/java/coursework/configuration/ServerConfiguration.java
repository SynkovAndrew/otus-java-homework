package com.otus.java.coursework.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Configuration
public class ServerConfiguration {
    @Bean
    public ExecutorService executorService() {
        return newSingleThreadExecutor();
    }
}
