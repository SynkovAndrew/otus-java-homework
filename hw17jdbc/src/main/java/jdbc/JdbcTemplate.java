package jdbc;

import annotation.Id;
import exception.IdAnnotaionIsNotPresentedException;
import exception.IdFieldIsNullException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static jdbc.SQLUtils.*;
import static reflection.ReflectionUtils.*;

public class JdbcTemplate<T> implements AutoCloseable {
    private final Connection connection;
    private final ClassesMetaDataCache cache;

    public JdbcTemplate(final Connection connection) {
        this.connection = connection;
        this.cache = new ClassesMetaDataCache();
    }

    public void create(final T object) {
        final Optional<Savepoint> savepoint = getSavePoint(connection);
        final String insertStatement = cache.getInsertStatement(object.getClass());
        try (final var preparedStatement = connection.prepareStatement(insertStatement)) {
            final var index = new AtomicInteger(1);
            cache.getFields(object.getClass()).stream()
                    .filter(field -> !field.isAnnotationPresent(Id.class))
                    .map(field -> getFieldValue(field, object))
                    .forEach(value -> setObject(preparedStatement, index.getAndIncrement(), value.get()));
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            savepoint.ifPresent(sp -> rollbackSavePoint(connection, sp));
            System.err.println(e.getMessage());
        }
    }

    public void update(final T object) {
        final Optional<Savepoint> savepoint = getSavePoint(connection);
        final String updateStatement = cache.getUpdateStatement(object.getClass());
        try (final var preparedStatement = connection.prepareStatement(updateStatement)) {
            final var index = new AtomicInteger(1);
            final Field idField = cache.getIdField(object.getClass())
                    .orElseThrow(IdAnnotaionIsNotPresentedException::new);
            cache.getFields(object.getClass()).stream()
                    .filter(field -> !field.isAnnotationPresent(Id.class))
                    .map(field -> getFieldValue(field, object))
                    .forEach(value -> setObject(preparedStatement, index.getAndIncrement(), value.get()));
            setObject(preparedStatement, index.getAndIncrement(),
                    getFieldValue(idField, object).orElseThrow(IdFieldIsNullException::new));
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            savepoint.ifPresent(sp -> rollbackSavePoint(connection, sp));
            System.err.println(e.getMessage());
        }
    }

    public boolean exists(final Long id, final Class clazz) {
        final var checkIfExistsStatement = cache.getCheckIfExistsStatement(clazz);
        try (final var preparedStatement = connection.prepareStatement(checkIfExistsStatement)) {
            preparedStatement.setObject(1, id);
            final var resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public void createOrUpdate(final T object) {
        final Object id = cache.getIdField(object.getClass())
                .map(field -> getFieldValue(field, object)
                        .orElseThrow(IdFieldIsNullException::new))
                .orElseThrow(IdAnnotaionIsNotPresentedException::new);
        final boolean isUpdate = exists((Long) id, object.getClass());
        if (isUpdate) {
            update(object);
        } else {
            create(object);
        }
    }

    public T load(final long id, final Class<T> clazz) {
        try (final var preparedStatement = connection.prepareStatement(cache.getSelectOneStatement(clazz))) {
            setObject(preparedStatement, 1, id);
            return mapResultSetToObjects(preparedStatement.executeQuery(), clazz).stream()
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public List<T> loadAll(Class<T> clazz) {
        try (final var preparedStatement = connection.prepareStatement(cache.getSelectAllStatement(clazz))) {
            return mapResultSetToObjects(preparedStatement.executeQuery(), clazz);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return emptyList();
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    private List<T> mapResultSetToObjects(final ResultSet resultSet, final Class<T> clazz) throws SQLException {
        final List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            final T object = createObject(clazz);
            for (Field field : cache.getFields(clazz)) {
                final Object value = resultSet.getObject(field.getName());
                setFieldValue(field, object, value);
            }
            result.add(object);
        }
        return result;
    }
}
