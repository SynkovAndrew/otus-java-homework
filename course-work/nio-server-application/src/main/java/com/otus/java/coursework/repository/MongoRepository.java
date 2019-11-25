package com.otus.java.coursework.repository;

import java.util.List;

public interface MongoRepository<T> {
    List<T> findAll();

    T save(T entity);
}