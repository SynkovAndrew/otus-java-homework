package com.otus.java.ioc.service;

import com.otus.java.ioc.cache.UserCacheEngine;
import com.otus.java.ioc.repository.UserDAO;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.DbServiceImpl;

@Service
public class UserService extends DbServiceImpl<User> {
    @Autowired
    public UserService(final UserDAO dao, final UserCacheEngine cacheEngine) {
        super(dao, cacheEngine);
    }
}
