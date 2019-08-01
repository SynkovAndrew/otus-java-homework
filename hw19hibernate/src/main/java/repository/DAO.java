package repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Table;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static utils.StringUtils.capitalize;

@RequiredArgsConstructor
public class DAO {
    private final SessionFactory sessionFactory;

    public <T> void create(T object) {
        Transaction transaction = null;
        try (final var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(object);
            transaction.commit();
        } catch (Exception e) {
            ofNullable(transaction).ifPresent(Transaction::rollback);
        }
    }


    public <T> void update(T object) {
        Transaction transaction = null;
        try (final var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(object);
            transaction.commit();
        } catch (Exception e) {
            ofNullable(transaction).ifPresent(Transaction::rollback);
        }
    }

    public <T> T load(long id, Class<T> clazz) {
        Transaction transaction = null;
        try (final var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            final T obj = session.get(clazz, id);
            transaction.commit();
            return obj;
        } catch (Exception e) {
            ofNullable(transaction).ifPresent(Transaction::rollback);
            return null;
        }
    }

    public <T> List<T> loadAll(Class<T> clazz) {
        Transaction transaction = null;
        final String tableName = capitalize(clazz.getAnnotation(Table.class).name());
        try (final var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            final List<T> resultList = session.createQuery(String.format("from %s", tableName), clazz).getResultList();
            transaction.commit();
            return resultList;
        } catch (Exception e) {
            ofNullable(transaction).ifPresent(Transaction::rollback);
            return emptyList();
        }
    }

    public <T> void removeAll(Class<T> clazz) {
        Transaction transaction = null;
        final String tableName = capitalize(clazz.getAnnotation(Table.class).name());
        try (final var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery(String.format("delete from %s", tableName)).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            ofNullable(transaction).ifPresent(Transaction::rollback);
        }
    }
}
