package com.otus.java.coursework.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
abstract class AbstractMongoRepository<T> {
    protected final MongoClient mongoClient;
    protected final String collectionName;
    @Value("${database.name}")
    private String databaseName;

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    protected abstract Document map(final T entity);

    public T save(final T entity) {
        getCollection().insertOne(map(entity));
        return entity;
    }
}
