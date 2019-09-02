package databaseclient.service;

import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static service.MappingService.map;
import static service.MappingService.mapAsResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceAdapter {
    private final UserService userService;

    public void createUser(final CreateUserRequestDTO request) {
        final var user = map(request);
        userService.create(user);
        log.info("User's been created: {}", user);
    }

    public FindUsersResponseDTO findUsers() {
        final var users = userService.loadAll();
        log.info("Users've been loaded: {}", users);
        return mapAsResponse(users);
    }
}
