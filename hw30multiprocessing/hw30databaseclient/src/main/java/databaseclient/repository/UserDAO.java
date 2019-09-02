package databaseclient.repository;

import domain.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import repository.DAO;

@Repository
public class UserDAO extends DAO<User> {
    public UserDAO(final SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }
}
