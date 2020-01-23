package com.otus.java.coursework.repository;


import com.mongodb.MongoClient;
import com.otus.java.coursework.domain.User;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "database")
public class UserRepositoryImpl extends AbstractMongoRepository<User> {
    public UserRepositoryImpl(final MongoClient mongoClient) {
        super(mongoClient, User.class.getSimpleName().toLowerCase());
    }

    @Override
    protected Document map(User entity) {
        final var document = new Document();
        document.append("age", entity.getAge());
        document.append("name", entity.getName());
        document.append("userId", entity.getUserId());
        return document;
    }

    @Override
    protected User map(Document document) {
        return User.builder()
                .age(document.getInteger("age"))
                .userId(document.getLong("userId"))
                .name(document.getString("name"))
                .build();
    }
}