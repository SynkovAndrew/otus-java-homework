package databaseclient.configuration;

import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class DatabaseConfiguration {
    @Bean
    public SessionFactory sessionFactory() {
        final var configuration = new org.hibernate.cfg.Configuration().configure("hibernate.cfg.xml");
        final var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        Stream.of(Address.class, Phone.class, User.class).forEach(metadataSources::addAnnotatedClass);
        final Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }
}
