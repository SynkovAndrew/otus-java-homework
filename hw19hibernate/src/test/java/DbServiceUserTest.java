import domain.Address;
import domain.Phone;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.DBServiceInterface;
import service.DbService;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DbServiceUserTest {
    private DBServiceInterface service;

    @BeforeEach
    public void beforeEach() {
        service = DbService.build();
    }

    @AfterEach
    public void afterEach() {
        service.removeAll(User.class);
    }

    @Test
    public void createAndLoadTest() {
        final var user = User.builder()
                .age(31)
                .name("Pavel")
                .build();
        final var address = Address.builder()
                .street("Abegory st.")
                .user(user)
                .build();
        final var phone1 = Phone.builder()
                .number("+79345234568")
                .user(user)
                .build();
        final var phone2 = Phone.builder()
                .number("+79341111118")
                .user(user)
                .build();
        user.setAddress(address);
        user.setPhones(newArrayList(phone1, phone2));
        service.create(user);

        final User loaded = service.loadAll(User.class).stream().findFirst().get();

        assertNotNull(loaded);
        assertNotNull(loaded.getId());
        assertNotNull(loaded.getAddress());
        assertNotNull(loaded.getAddress().getId());
        assertNotNull(loaded.getPhones());
        assertNotNull(loaded.getPhones().get(0).getId());
        assertNotNull(loaded.getPhones().get(1).getId());
        assertEquals(2, loaded.getPhones().size());
        assertEquals(31, loaded.getAge());
        assertEquals("Pavel", loaded.getName());
        assertEquals("Abegory st.", loaded.getAddress().getStreet());
        assertEquals("+79345234568", loaded.getPhones().get(0).getNumber());
        assertEquals("+79341111118", loaded.getPhones().get(1).getNumber());
    }

    @Test
    public void updateTest() {
        final var user = User.builder()
                .age(31)
                .name("Pavel")
                .build();
        final var address = Address.builder()
                .street("Abegory st.")
                .user(user)
                .build();
        final var phone1 = Phone.builder()
                .number("+79345234568")
                .user(user)
                .build();
        final var phone2 = Phone.builder()
                .number("+79341111118")
                .user(user)
                .build();
        user.setAddress(address);
        user.setPhones(newArrayList(phone1, phone2));
        service.create(user);

        user.setAge(22);
        user.setName("Max");
        phone1.setNumber("+799999999999");
        phone2.setNumber("+711111111");
        address.setStreet("Poltava st.");
        service.update(user);

        final User loaded = service.loadAll(User.class).stream().findFirst().get();

        assertNotNull(loaded);
        assertNotNull(loaded.getId());
        assertNotNull(loaded.getAddress());
        assertNotNull(loaded.getAddress().getId());
        assertNotNull(loaded.getPhones());
        assertNotNull(loaded.getPhones().get(0).getId());
        assertNotNull(loaded.getPhones().get(1).getId());
        assertEquals(2, loaded.getPhones().size());
        assertEquals(22, loaded.getAge());
        assertEquals("Max", loaded.getName());
        assertEquals("Poltava st.", loaded.getAddress().getStreet());
        assertEquals("+799999999999", loaded.getPhones().get(0).getNumber());
        assertEquals("+711111111", loaded.getPhones().get(1).getNumber());
    }
}
