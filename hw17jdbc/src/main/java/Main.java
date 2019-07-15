public class Main {
    public static void main(String[] args) {
        final var connection = SQLUtils.getConnection();
        final int result = SQLUtils.executeUpdate(connection, Statement.CREATE_USER_TABLE);
    }
}
