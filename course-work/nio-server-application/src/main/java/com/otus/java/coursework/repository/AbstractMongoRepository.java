package com.otus.java.coursework.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.apache.commons.compress.utils.Lists.newArrayList;

@RequiredArgsConstructor
abstract class AbstractMongoRepository<T> implements MongoRepository<T> {
    protected final MongoClient mongoClient;
    protected final String collectionName;
    @Value("${database.name}")
    private String databaseName;

    public List<T> findAll() {
        final List<T> result = newArrayList();
        final var cursor = getCollection().find().cursor();
        while (cursor.hasNext()) {
            result.add(map(cursor.next()));
        }
        return result;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    protected abstract Document map(final T entity);

    protected abstract T map(final Document document);

    public T save(final T entity) {
        getCollection().insertOne(map(entity));
        return entity;
    }

    public void removeAll() {
        getCollection().deleteMany(new BasicDBObject());
    }
}
