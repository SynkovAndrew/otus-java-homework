package service;

import java.util.List;

public interface DBServiceInterface<T> {
    void create(T object);

    void update(T object);

    void createOrUpdate(T object);

    <T> T load(long id, Class<T> clazz);

    <T> List<T> loadAll(Class<T> clazz);
}

