import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUtils {
    public static Connection getConnection() {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:")) {
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static int executeUpdate(final Connection connection, final String statement) {
        try (var preparedStatement = connection.prepareStatement(statement)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
