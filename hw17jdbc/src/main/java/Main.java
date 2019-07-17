import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:")) {
            connection.setAutoCommit(false);
            SQLUtils.executeUpdate(connection, Statement.CREATE_USER_TABLE);
            SQLUtils.executeUpdate(connection,
                    String.format(Statement.INSERT_INTO_USER_TABLE, "Andrey", 24));
            SQLUtils.executeUpdate(connection,
                    String.format(Statement.INSERT_INTO_USER_TABLE, "Vlad", 21));
            SQLUtils.executeQuery(connection, Statement.SELECT_ALL_FROM_USER).forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
