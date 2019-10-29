package com.otus.java.multiprocessing.databaseclient.cache;

import cache.CacheEngineImpl;
import domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserCacheEngine extends CacheEngineImpl<Long, User> {
    private final static int MAX_ELEMENT = 100000;

    public UserCacheEngine() {
        super(MAX_ELEMENT);
    }
}
