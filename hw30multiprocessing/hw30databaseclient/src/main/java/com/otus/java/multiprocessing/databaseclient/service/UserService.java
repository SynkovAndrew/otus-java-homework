package com.otus.java.multiprocessing.databaseclient.service;


import com.otus.java.multiprocessing.databaseclient.cache.UserCacheEngine;
import com.otus.java.multiprocessing.databaseclient.repository.UserDAO;
import domain.User;
import org.springframework.stereotype.Service;
import service.DbServiceImpl;

@Service
public class UserService extends DbServiceImpl<User> {
    public UserService(final UserDAO dao, final UserCacheEngine cacheEngine) {
        super(dao, cacheEngine);
    }
}
