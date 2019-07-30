package service;

import java.util.List;

public interface DBServiceInterface {
    <T> void create(T object);

    <T> void update(T object);

    <T> T load(long id, Class<T> clazz);

    <T> List<T> loadAll(Class<T> clazz);

    <T> void removeAll(Class<T> clazz);
}

