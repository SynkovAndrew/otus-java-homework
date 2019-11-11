package server;

import dto.CreateUserRequestDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerActionExecutor {
    private final Map<Integer, UserDTO> responses = new ConcurrentHashMap<>();
    private final UserService userService;

    public void acceptCreateUserRequest(final int clientId, final CreateUserRequestDTO request) {
        userService.create(request).subscribe(user -> responses.put(clientId, user));
    }

    public Optional<UserDTO> getResponse(final int id) {
        return Optional.ofNullable(responses.get(id));
    }
}
