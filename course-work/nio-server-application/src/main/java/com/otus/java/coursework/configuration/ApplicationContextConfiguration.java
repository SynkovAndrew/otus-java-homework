package com.otus.java.coursework.configuration;

import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.serialization.SerializerImpl;
import com.otus.java.coursework.service.ByteProcessorService;
import com.otus.java.coursework.service.ByteProcessorServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Configuration
public class ApplicationContextConfiguration {
    @Bean
    public ByteProcessorService byteProcessorService() {
        return new ByteProcessorServiceImpl();
    }

    @Bean
    public ExecutorService executorService() {
        return newSingleThreadExecutor();
    }

    @Bean
    public Serializer serializer() {
        return new SerializerImpl();
    }
}
