package jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static reflection.ReflectionUtils.createObject;
import static reflection.ReflectionUtils.setFieldValue;

public class SQLUtils {
    public static int executeUpdate(final Connection connection, final String statement) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    public static <T> List<T> executeQuery(final Connection connection,
                                           final String statement,
                                           final Class<T> clazz) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            final List<T> result = new ArrayList<>();
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final T object = createObject(clazz);
                for (Field field : clazz.getDeclaredFields()) {
                    final Object value = resultSet.getObject(field.getName());
                    setFieldValue(field, object, value);
                }
                result.add(object);
            }
            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
