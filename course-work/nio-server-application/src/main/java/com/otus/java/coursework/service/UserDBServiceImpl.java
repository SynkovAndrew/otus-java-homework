package com.otus.java.coursework.service;


import com.otus.java.coursework.domain.User;
import com.otus.java.coursework.repository.MongoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@ConditionalOnProperty(name = "server.action.executor.implementation", havingValue = "database")
public class UserDBServiceImpl implements UserDBService {
    private final AtomicLong id;
    private final MongoRepository<User> repository;

    public UserDBServiceImpl(final MongoRepository<User> repository) {
        this.repository = repository;
        this.id = new AtomicLong(10000);
    }

    @Override
    public Object create(final Object request) {
/*        final var user = Mapper.map(request);
        user.setUserId(id.getAndIncrement());
        return Mapper.map(repository.save(user));*/
        return request;
    }

    @Override
    public List<Object> findAll() {
        return List.of();
/*        return repository.findAll().stream()
                .map(Mapper::map)
                .collect(toList());*/
    }
}
