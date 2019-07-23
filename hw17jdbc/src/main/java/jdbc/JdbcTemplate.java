package jdbc;

import annotation.Id;
import reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static jdbc.SQLUtils.*;
import static reflection.ReflectionUtils.*;

public class JdbcTemplate<T> implements AutoCloseable {
    private final Connection connection;
    private final SQLCacheUtils SQLCacheUtils;

    public JdbcTemplate() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:mem:");
        this.SQLCacheUtils = new SQLCacheUtils();
        connection.setAutoCommit(false);
    }

    public void create(final T object) {
        final Optional<Savepoint> savepoint = getSavePoint(connection);
        final String insertStatement = SQLCacheUtils.getInsertStatement(object.getClass());
        try (final var preparedStatement = connection.prepareStatement(insertStatement)) {
            final var index = new AtomicInteger(1);
            SQLCacheUtils.getFields(object.getClass()).stream()
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
        final String updateStatement = SQLCacheUtils.getUpdateStatement(object.getClass());
        try (final var preparedStatement = connection.prepareStatement(updateStatement)) {
            final var index = new AtomicInteger(1);
            final Field idField = SQLCacheUtils.getIdField(object.getClass()).get();
            SQLCacheUtils.getFields(object.getClass()).stream()
                    .filter(field -> !field.isAnnotationPresent(Id.class))
                    .map(field -> getFieldValue(field, object))
                    .forEach(value -> setObject(preparedStatement, index.getAndIncrement(), value.get()));
            setObject(preparedStatement, index.getAndIncrement(), getFieldValue(idField, object).get());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            savepoint.ifPresent(sp -> rollbackSavePoint(connection, sp));
            System.err.println(e.getMessage());
        }
    }

    public void createOrUpdate(final T object) {
        boolean isUpdate = false;
        final var checkIfExistsStatement = SQLCacheUtils.getCheckIfExistsStatement(object.getClass());
        try (final var preparedStatement = connection.prepareStatement(checkIfExistsStatement)) {
            final Field idField = SQLCacheUtils.getIdField(object.getClass()).get();
            preparedStatement.setObject(1, ReflectionUtils.getFieldValue(idField, object).get());
            final var resultSet = preparedStatement.executeQuery();
            isUpdate = resultSet.next();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        if (isUpdate) {
            update(object);
        } else {
            create(object);
        }
    }

    public <T> T load(final long id, final Class<T> clazz) {
        try (final var preparedStatement = connection.prepareStatement(SQLCacheUtils.getSelectOneStatement(clazz))) {
            setObject(preparedStatement, 1, id);
            return mapResultSetToObjects(preparedStatement.executeQuery(), clazz).stream()
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public <T> List<T> loadAll(Class<T> clazz) {
        try (final var preparedStatement = connection.prepareStatement(SQLCacheUtils.getSelectAllStatement(clazz))) {
            return mapResultSetToObjects(preparedStatement.executeQuery(), clazz);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    private <T> List<T> mapResultSetToObjects(final ResultSet resultSet, final Class<T> clazz) throws SQLException {
        final List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            final T object = createObject(clazz);
            for (Field field : clazz.getDeclaredFields()) {
                final Object value = resultSet.getObject(field.getName());
                setFieldValue(field, object, value);
            }
            result.add(object);
        }
        return result;
    }
}
