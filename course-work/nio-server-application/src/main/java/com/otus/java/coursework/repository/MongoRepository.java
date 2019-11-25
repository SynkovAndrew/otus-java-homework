package com.otus.java.coursework.repository;

public interface MongoRepository<T> {
    T save(T entity);
}