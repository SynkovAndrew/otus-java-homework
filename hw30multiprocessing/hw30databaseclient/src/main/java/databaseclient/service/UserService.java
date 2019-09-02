package databaseclient.service;


import databaseclient.cache.UserCacheEngine;
import databaseclient.repository.UserDAO;
import domain.User;
import org.springframework.stereotype.Service;
import service.DbServiceImpl;

@Service
public class UserService extends DbServiceImpl<User> {
    public UserService(final UserDAO dao, final UserCacheEngine cacheEngine) {
        super(dao, cacheEngine);
    }
}
