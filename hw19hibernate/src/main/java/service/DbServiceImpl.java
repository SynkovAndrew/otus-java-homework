package service;

import lombok.RequiredArgsConstructor;
import repository.DAO;

import java.util.List;

@RequiredArgsConstructor
public class DbServiceImpl<T> implements DBService<T> {
    private final DAO<T> dao;

    @Override
    public void create(final T object) {
        dao.create(object);
    }

    @Override
    public void update(T object) {
        dao.update(object);
    }

    @Override
    public T load(final long id, final Class<T> clazz) {
        return dao.load(id, clazz);
    }

    @Override
    public List<T> loadAll(final Class<T> clazz) {
        return dao.loadAll(clazz);
    }

    @Override
    public void removeAll(Class<T> clazz) {
        dao.removeAll(clazz);
    }
}
