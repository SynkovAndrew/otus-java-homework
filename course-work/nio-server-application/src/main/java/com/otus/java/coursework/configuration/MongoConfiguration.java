package com.otus.java.coursework.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "database")
public class MongoConfiguration {
    @Bean
    public MongoClient mongoClient(final @Value("${database.uri}") String uri) {
        return new MongoClient(new MongoClientURI(uri));
    }
}
