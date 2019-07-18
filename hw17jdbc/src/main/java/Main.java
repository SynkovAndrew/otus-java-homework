import domain.User;
import jdbc.InitializeStatement;
import jdbc.JdbcTemplate;
import jdbc.SQLUtils;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (var template = new JdbcTemplate<User>()) {
            SQLUtils.executeUpdate(template.getConnection(), InitializeStatement.CREATE_USER_TABLE);

            template.create(User.builder().age(11).name("Pavel").build());
            template.create(User.builder().age(21).name("Faver").build());

            template.loadAll(User.class).forEach(System.out::println);

            template.update(User.builder().age(21).name("Maxim").id(2L).build());

            template.loadAll(User.class).forEach(System.out::println);

            template.createOrUpdate(User.builder().age(21).name("Alex").id(2L).build());

            template.loadAll(User.class).forEach(System.out::println);

            template.createOrUpdate(User.builder().age(31).name("Mavrodi").id(101L).build());

            template.loadAll(User.class).forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
