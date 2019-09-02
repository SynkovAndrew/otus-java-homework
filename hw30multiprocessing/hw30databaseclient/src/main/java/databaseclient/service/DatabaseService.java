package databaseclient.service;

import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.AbstractMessageService;
import messageV2.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import static messageV2.Queue.OUTPUT_QUEUE;

@Service
@Slf4j
public class DatabaseService extends AbstractMessageService {
    private final UserServiceAdapter userService;

    public DatabaseService(UserServiceAdapter userService) throws IOException {
        super();
        this.userService = userService;
    }

    @Override
    protected void handleInputQueueMessage(final Message<? extends ParentDTO> message) {
        log.info("Message's been received: {}", message);

        // TODO: Think about a way to overcome class casting !!!
        userService.createUser((CreateUserRequestDTO) message.getContent());

        final FindUsersResponseDTO users = userService.findUsers();
        final Message<FindUsersResponseDTO> outputMessage = new Message<>(users);
        queues.get(OUTPUT_QUEUE).add(outputMessage);
        log.info("Message's been sent: {}", outputMessage);
    }

    @Override
    protected void handleOutputQueueMessage(final Message<? extends ParentDTO> message) {

    }
}
