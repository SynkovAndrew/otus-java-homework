package configuration;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class HibernateConfigurationFactory {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(final String configName, Class... classes) {
        return ofNullable(sessionFactory)
                .orElseGet(() -> {
                    final var configuration = new Configuration().configure(configName);
                    final var serviceRegistry = new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties())
                            .build();
                    final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                    Stream.of(classes).forEach(metadataSources::addAnnotatedClass);
                    final Metadata metadata = metadataSources.getMetadataBuilder().build();
                    return sessionFactory = metadata.getSessionFactoryBuilder().build();
                });
    }
}
