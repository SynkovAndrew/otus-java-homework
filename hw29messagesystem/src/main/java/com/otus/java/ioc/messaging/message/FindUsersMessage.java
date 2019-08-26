package com.otus.java.ioc.messaging.message;

import com.otus.java.ioc.dto.FindUsersResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static com.otus.java.ioc.messaging.message.Queue.FRONT_END_QUEUE;

@Data
@Builder
@AllArgsConstructor
public class FindUsersMessage implements Message<FindUsersResponseDTO> {
    private FindUsersResponseDTO content;

    @Override
    public Queue getTargetQueue() {
        return FRONT_END_QUEUE;
    }

    @Override
    public FindUsersResponseDTO getContent() {
        return content;
    }
}

