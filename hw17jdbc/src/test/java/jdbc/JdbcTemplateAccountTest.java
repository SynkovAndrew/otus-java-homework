package jdbc;

import domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static jdbc.SQLUtils.executeUpdate;

public class JdbcTemplateAccountTest {
    private JdbcTemplate<Account> template;

    @BeforeEach
    public void beforeEach() throws SQLException {
        template = new JdbcTemplate<>();
        executeUpdate(template.getConnection(), InitializeStatement.CREATE_ACCOUNT_TABLE);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        template.close();
    }

    @Test
    public void createTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("ADMIN").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("USER").build());

        final List<Account> expected = new ArrayList<>();
        expected.add(Account.builder().rest(BigDecimal.valueOf(100)).type("ADMIN").number(1L).build());
        expected.add(Account.builder().rest(BigDecimal.valueOf(300)).type("USER").number(2L).build());

        accounts.forEach(template::create);

        final List<Account> result = template.loadAll(Account.class);

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(2, accounts.size());
        Assertions.assertIterableEquals(expected, result);
    }

    @Test
    public void updateTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("USER").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").build());

        final List<Account> expected = new ArrayList<>();
        expected.add(Account.builder().rest(BigDecimal.valueOf(1000)).type("ADMIN").number(1L).build());
        expected.add(Account.builder().rest(BigDecimal.valueOf(3000)).type("USER").number(2L).build());

        accounts.forEach(template::create);
        expected.forEach(template::update);

        final List<Account> result = template.loadAll(Account.class);

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(2, accounts.size());
        Assertions.assertIterableEquals(expected, result);
    }

    @Test
    public void loadTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("USER").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").build());

        accounts.forEach(template::create);

        final Account result = template.load(2L, Account.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").number(2L).build(), result);
    }

    @Test
    public void createOrUpdateTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("USER").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").build());

        accounts.forEach(template::create);

        template.createOrUpdate(Account.builder().rest(BigDecimal.valueOf(99)).type("ADMIN").number(1L).build());
        template.createOrUpdate(Account.builder().rest(BigDecimal.valueOf(34)).type("CALLCENTER").number(60L).build());

        final Account admin = template.load(1L, Account.class);
        final Account callcenter = template.load(3L, Account.class);

        Assertions.assertNotNull(admin);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(99)).type("ADMIN").number(1L).build(), admin);
        Assertions.assertNotNull(callcenter);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(34)).type("CALLCENTER").number(3L).build(), callcenter);
    }
}
