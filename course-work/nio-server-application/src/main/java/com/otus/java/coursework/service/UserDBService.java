package com.otus.java.coursework.service;


import com.otus.java.coursework.domain.User;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import com.otus.java.coursework.repository.MongoRepository;
import com.otus.java.coursework.utils.Mapper;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserDBService {
    private final AtomicLong id;
    private final MongoRepository<User> repository;

    public UserDBService(final MongoRepository repository) {
        this.repository = repository;
        this.id = new AtomicLong(10000);
    }

    public UserDTO create(final CreateUserRequestDTO request) {
        final var user = Mapper.map(request);
        user.setUserId(id.getAndIncrement());
        return Mapper.map(repository.save(user));
    }
}
