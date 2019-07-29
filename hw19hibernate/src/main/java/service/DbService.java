package service;

import repository.DbRepository;

import java.util.List;

public class DbService<T> implements DBServiceInterface<T> {
    private final DbRepository<T> repository;

    public DbService() {
        repository = new DbRepository<>();
    }

    @Override
    public void create(T object) {
        repository.create(object);
    }

    @Override
    public void update(T object) {

    }

    @Override
    public void createOrUpdate(T object) {

    }

    @Override
    public boolean exists(Long id, Class clazz) {
        return false;
    }

    @Override
    public T load(long id, Class<T> clazz) {
        return repository.load(id, clazz);
    }

    @Override
    public List<T> loadAll(Class<T> clazz) {
        return null;
    }
}
