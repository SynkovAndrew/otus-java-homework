import domain.Address;
import domain.Phone;
import domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Main {

    public static void main(String[] args) {
        final var configuration = new Configuration().configure("hibernate.cfg.xml");
        final var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        final var metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Phone.class)
                .addAnnotatedClass(User.class)
                .getMetadataBuilder()
                .build();
        final SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

    }
}
