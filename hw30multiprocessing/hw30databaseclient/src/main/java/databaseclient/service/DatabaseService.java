package databaseclient.service;

import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.ParentDTO;
import lombok.extern.slf4j.Slf4j;
import messageV2.AbstractMessageService;
import messageV2.Message;
import org.springframework.stereotype.Service;

import static messageV2.MessageSocketService.sendMessage;
import static messageV2.Queue.OUTPUT_QUEUE;

@Service
@Slf4j
public class DatabaseService extends AbstractMessageService {
    private final UserServiceAdapter userService;

    public DatabaseService(UserServiceAdapter userService) {
        super();
        this.userService = userService;
    }

    @Override
    protected void handleInputQueueMessage(final Message<? extends ParentDTO> message) {
        log.info("Message's been received: {}", message);

        // TODO: Think about a way to overcome class casting !!!
        userService.createUser((CreateUserRequestDTO) message.getContent());

        final FindUsersResponseDTO response = userService.findUsers();
        final Message<FindUsersResponseDTO> outputMessage = new Message<>(response, response.getClass().getName());
        queues.get(OUTPUT_QUEUE).add(outputMessage);
        log.info("Message's been sent: {}", outputMessage);
    }

    @Override
    protected void handleOutputQueueMessage(final Message<? extends ParentDTO> message) {
        sendMessage(socket, message);
        log.info("Message's been sent to message server: {}", message);
    }
}
