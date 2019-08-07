package configuration;

import cache.CacheEngineImpl;
import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import repository.DAO;
import service.DbServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static configuration.HibernateConfigurationFactory.getSessionFactory;
import static java.util.Optional.ofNullable;

public class ServiceConfigurationFactory<T> {
    private Map<String, DbServiceImpl<T>> services = new HashMap<>();

    public DbServiceImpl<T> getDbService(final Class<T> clazz) {
        final String name = clazz.getSimpleName();
        return ofNullable(services.get(name))
                .orElseGet(() -> {
                    final DbServiceImpl<T> dbService = createDbService(clazz);
                    services.put(name, dbService);
                    return dbService;
                });
    }

    private DbServiceImpl<T> createDbService(final Class<T> clazz) {
        final SessionFactory sessionFactory = getSessionFactory(
                "hibernate.cfg.xml", Address.class, Phone.class, User.class);
        final CacheEngineImpl<Long, T> cacheEngine = new CacheEngineImpl<>(10000);
        final DAO<T> dao = new DAO<>(sessionFactory);
        final DbServiceImpl<T> dbService = new DbServiceImpl<>(dao, cacheEngine);
        return dbService;
    }
}
