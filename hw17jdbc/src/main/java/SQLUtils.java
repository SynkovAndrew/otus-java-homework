import domain.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUtils {
    public static int executeUpdate(final Connection connection, final String statement) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    public static List<User> executeQuery(final Connection connection, final String statement) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            final List<User> result = new ArrayList<>();
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age")));
            }
            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
