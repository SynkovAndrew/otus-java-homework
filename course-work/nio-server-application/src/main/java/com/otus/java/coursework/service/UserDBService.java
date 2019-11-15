package com.otus.java.coursework.service;


import com.otus.java.coursework.dto.CreateUserRequestDTO;
import com.otus.java.coursework.dto.UserDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.otus.java.coursework.repository.UserReactiveRepository;
import com.otus.java.coursework.utils.Mapper;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserDBService {
    private final AtomicLong id;
    private final UserReactiveRepository repository;

    public UserDBService(final UserReactiveRepository repository) {
        this.repository = repository;
        this.id = new AtomicLong(10000);
    }

    public Mono<UserDTO> create(final CreateUserRequestDTO request) {
        final var user = Mapper.map(request);
        user.setUserId(id.getAndIncrement());
        return repository.save(user).map(Mapper::map);
    }
}
