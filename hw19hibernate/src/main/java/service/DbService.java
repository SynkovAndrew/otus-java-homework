package service;

import repository.DAO;

import java.util.List;

public class DbService implements DBServiceInterface {
    private final DAO dao;

    public DbService() {
        dao = new DAO();
    }

    @Override
    public <T> void create(final T object) {
        dao.create(object);
    }

    @Override
    public <T> void update(T object) {

    }

    @Override
    public <T> void createOrUpdate(T object) {

    }

    @Override
    public <T> boolean exists(Long id, Class clazz) {
        return false;
    }

    @Override
    public <T> T load(final long id, final Class<T> clazz) {
        return dao.load(id, clazz);
    }

    @Override
    public <T> List<T> loadAll(final Class<T> clazz) {
        return dao.loadAll(clazz);
    }
}
