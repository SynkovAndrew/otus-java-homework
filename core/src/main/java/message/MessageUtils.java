package message;

import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;

public class MessageUtils {
    public static Message<CreateUserRequestDTO> createDatabaseMessage(final CreateUserRequestDTO content) {
        return new Message<>(content, Queue.DATABASE_QUEUE);
    }

    public static Message<FindUsersResponseDTO> createFrontEndMessage(final FindUsersResponseDTO content) {
        return new Message<>(content, Queue.FRONT_END_QUEUE);
    }

}
