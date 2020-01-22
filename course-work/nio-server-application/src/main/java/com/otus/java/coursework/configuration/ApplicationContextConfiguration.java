package com.otus.java.coursework.configuration;

import com.otus.java.coursework.serialization.Serializer;
import com.otus.java.coursework.serialization.SerializerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextConfiguration {
    @Bean
    public Serializer serializer() {
        return new SerializerImpl();
    }
}
