import com.otus.java.ioc.cache.UserCacheEngine;
import com.otus.java.ioc.configuration.ApplicationContextConfiguration;
import com.otus.java.ioc.service.UserService;
import domain.Address;
import domain.Phone;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Duration;
import java.time.Instant;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationContextConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class DbServiceImplUserTest {
    @Autowired
    private UserCacheEngine cacheEngine;
    @Autowired
    private UserService service;

    @AfterEach
    public void afterEach() {
        service.removeAll();
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

        final User loaded = service.loadAll().stream().findFirst().get();

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

        final User loaded = service.loadAll().stream().findFirst().get();

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
        final int ITERATION_COUNT = 10000;
        for (int i = 1; i <= ITERATION_COUNT; i++) {
            service.create(User.builder()
                    .name("User " + i)
                    .age(i)
                    .build());
        }

        long fromDbTotal = 0;
        long fromCacheTotal = 0;

        for (long i = 1; i <= ITERATION_COUNT; i++) {
            Instant start = Instant.now();
            service.load(i);
            Instant finish = Instant.now();
            fromDbTotal = fromDbTotal + Duration.between(start, finish).toNanos();
        }

        for (long i = 1; i <= ITERATION_COUNT; i++) {
            Instant start = Instant.now();
            service.load(i);
            Instant finish = Instant.now();
            fromCacheTotal = fromCacheTotal + Duration.between(start, finish).toNanos();
        }

        System.out.println();
        System.out.println();
        System.out.println("Loading from db avg. time: " + fromDbTotal / ITERATION_COUNT + " nanos");
        System.out.println("Loading from cache avg. time: " + fromCacheTotal / ITERATION_COUNT + " nanos");
        System.out.println("Hit count: " + cacheEngine.getHitCount());
        System.out.println("Miss count: " + cacheEngine.getMissCount());
        System.out.println();
        System.out.println();
    }


}
