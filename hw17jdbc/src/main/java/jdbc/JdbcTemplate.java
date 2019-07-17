package jdbc;

import reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class JdbcTemplate<T> implements AutoCloseable {
    private final String INSERT_INTO_TABLE_STATEMENT = "insert into %s (%s) values (%s)";
    private final Connection connection;

    public JdbcTemplate() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:mem:");
        connection.setAutoCommit(false);
    }

    public void create(T objectData) {
        final String name = objectData.getClass().getName();
        final List<Field> declaredFields = Arrays.asList(objectData.getClass().getDeclaredFields());

        final String colums = declaredFields.stream()
                .map(ReflectionUtils::getFieldName)
                .collect(joining(","));

        final String values = declaredFields.stream()
                .map(field -> ReflectionUtils.getFieldValue(field, objectData).map(Object::toString).orElse(""))
                .collect(joining(","));

        try (var ps = connection.prepareStatement(String.format(INSERT_INTO_TABLE_STATEMENT, name, colums, values))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    void update(T objectData) {

    }

    public void createOrUpdate(T objectData) {

    }

    public <T> T load(long id, Class<T> clazz) {

        return null;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
