package jdbc;

import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static jdbc.SQLUtils.executeUpdate;

public class JdbcTemplateUserTest {
    private JdbcTemplate<User> template;

    @BeforeEach
    public void beforeEach() throws SQLException {
        template = new JdbcTemplate<>();
        executeUpdate(template.getConnection(), InitializeStatement.CREATE_USER_TABLE);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        template.close();
    }

    @Test
    public void createTest() {
        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        final List<User> expected = new ArrayList<>();
        expected.add(User.builder().age(31).name("Pavel").id(1L).build());
        expected.add(User.builder().age(21).name("Tony").id(2L).build());
        expected.add(User.builder().age(41).name("John").id(3L).build());

        users.forEach(template::create);

        final List<User> result = template.loadAll(User.class);

        Assertions.assertNotNull(users);
        Assertions.assertEquals(3, users.size());
        Assertions.assertIterableEquals(expected, result);
    }

    @Test
    public void updateTest() {
        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        final List<User> expected = new ArrayList<>();
        expected.add(User.builder().age(31).name("Vasili").id(1L).build());
        expected.add(User.builder().age(21).name("Mart").id(2L).build());
        expected.add(User.builder().age(41).name("Cash").id(3L).build());

        users.forEach(template::create);
        expected.forEach(template::update);

        final List<User> result = template.loadAll(User.class);

        Assertions.assertNotNull(users);
        Assertions.assertEquals(3, users.size());
        Assertions.assertIterableEquals(expected, result);
    }

    @Test
    public void loadTest() {
        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        final List<User> expected = new ArrayList<>();
        expected.add(User.builder().age(31).name("Vasili").id(1L).build());

        users.forEach(template::create);
        expected.forEach(template::update);

        final User result = template.load(2L, User.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(User.builder().age(21).name("Tony").id(2L).build(), result);
    }

    @Test
    public void createOrUpdateTest() {
        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        users.forEach(template::create);

        template.createOrUpdate(User.builder().age(99).name("Dilan").id(1L).build());
        template.createOrUpdate(User.builder().age(34).name("Dah").id(6L).build());

        final User dilan = template.load(1L, User.class);
        final User dah = template.load(4L, User.class);

        Assertions.assertNotNull(dilan);
        Assertions.assertEquals(User.builder().age(99).name("Dilan").id(1L).build(), dilan);
        Assertions.assertNotNull(dah);
        Assertions.assertEquals(User.builder().age(34).name("Dah").id(4L).build(), dah);
    }


}
