package service;

import cache.CacheEngine;
import lombok.RequiredArgsConstructor;
import repository.DAO;

import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class DbServiceImpl<T> implements DBService<T> {
    private final DAO<T> dao;
    private final CacheEngine<Long, T> cacheEngine;

    @Override
    public void create(final T object) {
        dao.create(object);
    }

    @Override
    public void update(T object) {
        dao.update(object);
    }

    @Override
    public T load(final long id) {
        return ofNullable(cacheEngine.get(id))
                .orElseGet(() -> {
                    final var obj = dao.load(id);
                    cacheEngine.put(id, obj);
                    return obj;
                });

    }

    @Override
    public List<T> loadAll() {
        return dao.loadAll();
    }

    @Override
    public void removeAll() {
        dao.removeAll();
        cacheEngine.dispose();
    }
}
