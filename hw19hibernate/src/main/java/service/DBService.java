package service;

import java.util.List;

public interface DBService<T> {
    void create(T object);

    void update(T object);

    T load(long id);

    List<T> loadAll();

    void removeAll();
}

