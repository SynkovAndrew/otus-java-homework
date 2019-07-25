package service;

import java.util.List;

public interface DBServiceInterface<T> {
    void create(T object);

    void update(T object);

    void createOrUpdate(T object);

    boolean exists(final Long id, final Class clazz);

    T load(long id, Class<T> clazz);

    List<T> loadAll(Class<T> clazz);
}

