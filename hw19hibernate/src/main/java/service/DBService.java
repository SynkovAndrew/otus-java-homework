package service;

import java.util.List;

public interface DBService<T> {
    void create(T object);

    void update(T object);

    T load(long id, Class<T> clazz);

    List<T> loadAll(Class<T> clazz);

    void removeAll(Class<T> clazz);
}

