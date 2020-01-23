package com.otus.java.coursework.service;


import com.otus.java.coursework.domain.User;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import com.otus.java.coursework.repository.MongoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.otus.java.coursework.service.Mapper.map;
import static java.util.stream.Collectors.toList;

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
    public UserDTO create(final CreateUserRequestDTO request) {
        final var user = map(request);
        user.setUserId(id.getAndIncrement());
        return map(repository.save(user));
    }

    @Override
    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(Mapper::map)
                .collect(toList());
    }

    @PostConstruct
    public void init() {
        repository.removeAll();
    }
}
