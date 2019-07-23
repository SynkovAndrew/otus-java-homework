package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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

    public static Optional<Savepoint> getSavePoint(final Connection connection) {
        try {
            return ofNullable(connection.setSavepoint());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static void rollbackSavePoint(final Connection connection, final Savepoint savepoint) {
        try {
            connection.rollback(savepoint);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
