package com.otus.java.projectwork.nioserver.service;

import com.otus.java.projectwork.nioserver.dto.CreateUserRequestDTO;
import com.otus.java.projectwork.nioserver.dto.UserDTO;
import com.otus.java.projectwork.nioserver.repository.UserReactiveRepository;
import com.otus.java.projectwork.nioserver.utils.Mapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final AtomicLong id;
    private final UserReactiveRepository repository;

    public UserService(final UserReactiveRepository repository) {
        this.repository = repository;
        this.id = new AtomicLong(10000);
    }

    public Mono<UserDTO> create(final CreateUserRequestDTO request) {
        final var user = Mapper.map(request);
        user.setUserId(id.getAndIncrement());
        return repository.save(user).map(Mapper::map);
    }
}
