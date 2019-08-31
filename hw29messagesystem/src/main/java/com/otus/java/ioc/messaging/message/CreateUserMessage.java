package com.otus.java.ioc.messaging.message;

import dto.CreateUserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static com.otus.java.ioc.messaging.message.Queue.DATABASE_QUEUE;

@Data
@Builder
@AllArgsConstructor
public class CreateUserMessage implements Message<CreateUserRequestDTO> {
    private CreateUserRequestDTO content;

    @Override
    public Queue getTargetQueue() {
        return DATABASE_QUEUE;
    }

    @Override
    public CreateUserRequestDTO getContent() {
        return content;
    }
}

