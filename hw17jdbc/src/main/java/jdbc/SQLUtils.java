package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUtils {
    public static void setObject(final PreparedStatement preparedStatement, final int index, final Object object) {
        try {
            preparedStatement.setObject(index, object);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static int executeUpdate(final Connection connection, final String statement) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    public static int executeUpdate(final PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }
}
