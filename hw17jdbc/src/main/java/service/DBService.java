package service;

import jdbc.JdbcTemplate;

import java.sql.Connection;
import java.util.List;

public class DBService<T> implements DBServiceInterface<T> {
    private final JdbcTemplate<T> template;

    public DBService(final Connection connection) {
        this.template = new JdbcTemplate<>(connection);
    }

    @Override
    public void create(final T object) {
        template.create(object);
    }

    @Override
    public void update(final T object) {
        template.update(object);
    }

    @Override
    public void createOrUpdate(final T object) {
        template.createOrUpdate(object);
    }

    @Override
    public boolean exists(final Long id, final Class clazz) {
        return template.exists(id, clazz);
    }

    @Override
    public T load(final long id, final Class<T> clazz) {
        return template.load(id, clazz);
    }

    @Override
    public List<T> loadAll(final Class<T> clazz) {
        return template.loadAll(clazz);
    }
}
