package repository;

import org.hibernate.SessionFactory;

import java.util.List;

import static configuration.HibernateConfiguration.configureSessionFactory;

public class DbRepository<T> {
    private final SessionFactory sessionFactory;

    public DbRepository() {
        this.sessionFactory = configureSessionFactory();
    }

    public void create(T object) {
        try (final var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }


    public void update(T object) {

    }

    public void createOrUpdate(T object) {

    }


    public boolean exists(Long id, Class clazz) {
        return false;
    }


    public T load(long id, Class<T> clazz) {
        try (final var session = sessionFactory.openSession()) {
            return session.get(clazz, id);
        }
    }


    public List<T> loadAll(Class<T> clazz) {
        return null;
    }
} 
