import cache.CacheEngineImpl;
import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.DAO;
import service.DBService;
import service.DbServiceImpl;

import java.time.Duration;
import java.time.Instant;

import static com.google.common.collect.Lists.newArrayList;
import static configuration.HibernateConfigurationFactory.getSessionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DbServiceImplUserTest {
    private DBService<User> service;

    @BeforeEach
    public void beforeEach() {
        final SessionFactory sessionFactory = getSessionFactory(
                "hibernate.cfg.xml", Address.class, Phone.class, User.class);
        final CacheEngineImpl<Long, User> cacheEngine = new CacheEngineImpl<>(20);
        final DAO<User> dao = new DAO<>(sessionFactory, cacheEngine);
        service = new DbServiceImpl<>(dao);
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

    @Test
    public void cacheTest() {
        for (int i = 1; i <= 10000; i++) {
            service.create(User.builder()
                    .name("User " + i)
                    .age(i)
                    .build());
        }

        long totalFromDb = 0;
        long totalFromCache = 0;

        for (long i = 1; i <= 10000; i++) {
            Instant start = Instant.now();
            service.load(i, User.class);
            Instant finish = Instant.now();
            totalFromDb = totalFromDb + Duration.between(start, finish).toNanos();
        }

        for (long i = 1; i <= 10000; i++) {
            Instant start = Instant.now();
            service.load(i, User.class);
            Instant finish = Instant.now();
            totalFromCache = totalFromCache + Duration.between(start, finish).toNanos();
        }

        System.out.println("Loading from db avg. time: " + totalFromDb / 10000 + " nanos");
        System.out.println("Loading from cache avg. time: " + totalFromCache / 10000 + " nanos");
    }



}
