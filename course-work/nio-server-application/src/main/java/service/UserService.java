package service;


import dto.CreateUserRequestDTO;
import dto.UserDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.UserReactiveRepository;
import utils.Mapper;

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
