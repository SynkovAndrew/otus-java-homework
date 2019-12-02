package com.otus.java.coursework.service;

import java.util.List;

public interface UserDBService {
    Object create(final Object request);

    List<Object> findAll();
}
