package service;

import java.util.List;

public interface DBServiceInterface {
    <T> void create(T object);

    <T> void update(T object);

    <T> void createOrUpdate(T object);

    <T> boolean exists(final Long id, final Class clazz);

    <T> T load(long id, Class<T> clazz);

    <T> List<T> loadAll(Class<T> clazz);
}

