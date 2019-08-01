package service;

import lombok.RequiredArgsConstructor;
import repository.DAO;

import java.util.List;

@RequiredArgsConstructor
public class DbServiceImpl implements DBService {
    private final DAO dao;

    @Override
    public <T> void create(final T object) {
        dao.create(object);
    }

    @Override
    public <T> void update(T object) {
        dao.update(object);
    }

    @Override
    public <T> T load(final long id, final Class<T> clazz) {
        return dao.load(id, clazz);
    }

    @Override
    public <T> List<T> loadAll(final Class<T> clazz) {
        return dao.loadAll(clazz);
    }

    @Override
    public <T> void removeAll(Class<T> clazz) {
        dao.removeAll(clazz);
    }
}
