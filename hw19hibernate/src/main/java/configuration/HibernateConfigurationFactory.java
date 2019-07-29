package configuration;

import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import static java.util.Optional.ofNullable;

public class HibernateConfigurationFactory {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return ofNullable(sessionFactory)
                .orElseGet(() -> {
                    final var configuration = new Configuration().configure("hibernate.cfg.xml");
                    final var serviceRegistry = new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties())
                            .build();
                    // the following classes are supposed to be in persistence context
                    final var metadata = new MetadataSources(serviceRegistry)
                            .addAnnotatedClass(Address.class)
                            .addAnnotatedClass(Phone.class)
                            .addAnnotatedClass(User.class)
                            .getMetadataBuilder()
                            .build();
                    sessionFactory = metadata.getSessionFactoryBuilder().build();
                    return sessionFactory;
                });
    }
}
