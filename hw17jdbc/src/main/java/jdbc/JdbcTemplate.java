package jdbc;

import annotation.Id;
import reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static jdbc.SQLUtils.executeQuery;
import static jdbc.SQLUtils.executeUpdate;
import static reflection.ReflectionUtils.*;
import static reflection.ReflectionUtils.getFieldName;
import static reflection.ReflectionUtils.getFieldValue;

public class JdbcTemplate<T> implements AutoCloseable {
    private final String INSERT_ONE_STATEMENT = "insert into %s (%s) values (%s)";
    private final String SELECT_ALL_STATEMENT = "select * from %s";
    private final String SELECT_ONE_BY_ID_STATEMENT = "select * from %s where %s = %d";
    private final String UPDATE_ONE_BY_ID_STATEMENT = "update %s set %s where %s = %d";
    private final Connection connection;

    public JdbcTemplate() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:mem:");
        connection.setAutoCommit(false);
        initTables();
    }

    public void create(final T object) {
        final String name = object.getClass().getSimpleName();
        final List<Field> declaredFields = Arrays.asList(object.getClass().getDeclaredFields());
        final String columns = declaredFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .map(ReflectionUtils::getFieldName)
                .collect(joining(","));
        final String values = declaredFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .map(field -> getFieldValue(field, object)
                        .map(o -> "\'" + o.toString() + "\'")
                        .orElse(""))
                .collect(joining(","));
        executeUpdate(connection, String.format(INSERT_ONE_STATEMENT, name, columns, values));
    }

    public void update(final T object) {
        final String name = object.getClass().getSimpleName();
        final List<Field> declaredFields = Arrays.asList(object.getClass().getDeclaredFields());
        final Field idField = getIdField(object.getClass()).get();

        final String matches = declaredFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .map(field -> getFieldValue(field, object)
                        .map(fieldValue -> getFieldName(field) + " = \'" + fieldValue + "\'").orElse(""))
                .collect(joining(","));
        executeUpdate(connection, String.format(UPDATE_ONE_BY_ID_STATEMENT, name, matches, idField.getName(), getFieldValue(idField, object).get()));
    }

    public void createOrUpdate(final T object) {
        final Long id = getIdField(object.getClass())
                .map(field -> (Long) getFieldValue(field, object).get())
                .orElse(null);
        if (isNull(load(id, object.getClass()))) {
            create(object);
        } else {
            update(object);
        }
    }

    public <T> T load(final long id, final Class<T> clazz) {
        final String name = clazz.getSimpleName();
        final String idFieldName = getIdField(clazz).map(Field::getName).orElse(null);
        final List<T> result = executeQuery(connection, String.format(SELECT_ONE_BY_ID_STATEMENT, name, idFieldName, id), clazz);
        return ofNullable(result).orElse(emptyList()).stream().findFirst().orElse(null);
    }

    public <T> List<T> loadAll(final Class<T> clazz) {
        final String name = clazz.getSimpleName();
        return executeQuery(connection, String.format(SELECT_ALL_STATEMENT, name), clazz);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    private void initTables() {
        SQLUtils.executeUpdate(connection, InitializeStatement.CREATE_USER_TABLE);
        SQLUtils.executeUpdate(connection, InitializeStatement.CREATE_ACCOUNT_TABLE);
    }

    private Optional<Field> getIdField(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst();
/*                .map(field -> getFieldValue(field, object).get())
                .orElse(null);*/
    }
}
