package jdbc;

import domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.DBService;
import service.DBServiceInterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static jdbc.SQLUtils.executeUpdate;

public class JdbcServiceAccountTest {
    private Connection connection;
    private DBServiceInterface<Account> service;

    @BeforeEach
    public void beforeEach() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        connection.setAutoCommit(false);
        service = new DBService<>(connection);
        executeUpdate(connection, InitializeStatement.CREATE_ACCOUNT_TABLE);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.close();
    }

    @Test
    public void createTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("ADMIN").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("USER").build());

        final List<Account> expected = new ArrayList<>();
        expected.add(Account.builder().rest(BigDecimal.valueOf(100)).type("ADMIN").number(1L).build());
        expected.add(Account.builder().rest(BigDecimal.valueOf(300)).type("USER").number(2L).build());

        accounts.forEach(service::create);

        final List<Account> result = service.loadAll(Account.class);

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

        accounts.forEach(service::create);
        expected.forEach(service::update);

        final List<Account> result = service.loadAll(Account.class);

        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(2, accounts.size());
        Assertions.assertIterableEquals(expected, result);
    }

    @Test
    public void loadTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("USER").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").build());

        accounts.forEach(service::create);

        final Account result = service.load(2L, Account.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").number(2L).build(), result);
    }

    @Test
    public void createOrUpdateTest() {
        final List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().rest(BigDecimal.valueOf(100)).type("USER").build());
        accounts.add(Account.builder().rest(BigDecimal.valueOf(300)).type("ADMIN").build());

        accounts.forEach(service::create);

        service.createOrUpdate(Account.builder().rest(BigDecimal.valueOf(99)).type("ADMIN").number(1L).build());
        service.createOrUpdate(Account.builder().rest(BigDecimal.valueOf(34)).type("CALLCENTER").number(60L).build());

        final Account admin = service.load(1L, Account.class);
        final Account callcenter = service.load(3L, Account.class);

        Assertions.assertNotNull(admin);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(99)).type("ADMIN").number(1L).build(), admin);
        Assertions.assertNotNull(callcenter);
        Assertions.assertEquals(Account.builder().rest(BigDecimal.valueOf(34)).type("CALLCENTER").number(3L).build(), callcenter);
    }
}
