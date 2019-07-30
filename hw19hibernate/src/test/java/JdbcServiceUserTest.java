import domain.Address;
import domain.Phone;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.DBServiceInterface;
import service.DbService;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcServiceUserTest {
    private DBServiceInterface service;

    @BeforeEach
    public void beforeEach() {
        service = new DbService();
    }

    @Test
    public void createTest() {
        final var address = Address.builder()
                .street("Abegory st.")
                .build();
        final var phone = Phone.builder()
                .number("+79345234568")
                .build();
        final var user = User.builder()
                .age(31)
                .name("Pavel")
                .address(address)
                .phones(Collections.singletonList(phone))
                .build();

        service.create(user);

        final List<User> users = service.loadAll(User.class);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertIterableEquals(newArrayList(User.builder()
                .id(1L)
                .age(31)
                .name("Pavel")
                .address(Address.builder()
                        .id(1L)
                        .street("Abegory st.")
                        .build())
                .phones(Collections.singletonList(Phone.builder()
                        .id(1L)
                        .number("+79345234568")
                        .build()))
                .build()), users);
    }

    @Test
    public void updateTest() {
/*        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        final List<User> expected = new ArrayList<>();
        expected.add(User.builder().age(31).name("Vasili").id(1L).build());
        expected.add(User.builder().age(21).name("Mart").id(2L).build());
        expected.add(User.builder().age(41).name("Cash").id(3L).build());

        users.forEach(service::create);
        expected.forEach(service::update);

        final List<User> result = service.loadAll(User.class);

        Assertions.assertNotNull(users);
        Assertions.assertEquals(3, users.size());
        Assertions.assertIterableEquals(expected, result);*/
    }

    @Test
    public void loadTest() {
        final var address = Address.builder()
                .street("Abegory st.")
                .build();
        final var phone = Phone.builder()
                .number("+79345234568")
                .build();
        final var user = User.builder()
                .age(31)
                .name("Pavel")
                .address(address)
                .phones(Collections.singletonList(phone))
                .build();

        service.create(user);

        final List<Address> addresses = service.loadAll(Address.class);
        final List<Phone> phones = service.loadAll(Phone.class);
        final List<User> users = service.loadAll(User.class);
        users.forEach(User::getPhones);

        assertNotNull(users);
    }

    @Test
    public void createOrUpdateTest() {
/*        final List<User> users = new ArrayList<>();
        users.add(User.builder().age(31).name("Pavel").build());
        users.add(User.builder().age(21).name("Tony").build());
        users.add(User.builder().age(41).name("John").build());

        users.forEach(service::create);

        service.createOrUpdate(User.builder().age(99).name("Dilan").id(1L).build());
        service.createOrUpdate(User.builder().age(34).name("Dah").id(6L).build());

        final User dilan = service.load(1L, User.class);
        final User dah = service.load(4L, User.class);

        Assertions.assertNotNull(dilan);
        Assertions.assertEquals(User.builder().age(99).name("Dilan").id(1L).build(), dilan);
        Assertions.assertNotNull(dah);
        Assertions.assertEquals(User.builder().age(34).name("Dah").id(4L).build(), dah);*/
    }
}
