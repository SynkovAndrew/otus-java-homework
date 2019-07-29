package configuration;

import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateConfiguration {
    public static SessionFactory configureSessionFactory() {
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
        return metadata.getSessionFactoryBuilder().build();
    }
}
