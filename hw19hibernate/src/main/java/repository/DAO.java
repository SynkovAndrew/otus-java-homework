package repository;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Table;
import java.util.List;

import static configuration.HibernateConfigurationFactory.getSessionFactory;
import static utils.StringUtils.capitalize;

public class DAO {
    private final SessionFactory sessionFactory;

    public DAO() {
        this.sessionFactory = getSessionFactory();
    }

    public <T> void create(T object) {
        try (final var session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            session.save(object);
            transaction.commit();
        }
    }


    public <T> void update(T object) {

    }

    public <T> void createOrUpdate(T object) {

    }


    public boolean exists(Long id, Class clazz) {
        return false;
    }


    public <T> T load(long id, Class<T> clazz) {
        try (final var session = sessionFactory.openSession()) {
            return session.get(clazz, id);
        }
    }


    public <T> List<T> loadAll(Class<T> clazz) {
        final String tableName = capitalize(clazz.getAnnotation(Table.class).name());
        try (final var session = sessionFactory.openSession()) {
            return session.createQuery(String.format("from %s", tableName), clazz).getResultList();
        }
    }
} 
