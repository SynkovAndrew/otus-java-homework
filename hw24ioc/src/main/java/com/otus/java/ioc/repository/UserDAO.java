package com.otus.java.ioc.repository;

import domain.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.DAO;

@Repository
public class UserDAO extends DAO<User> {
    public UserDAO(final @Autowired SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }
}
