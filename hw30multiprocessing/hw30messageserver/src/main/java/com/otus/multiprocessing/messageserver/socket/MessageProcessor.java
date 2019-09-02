package com.otus.multiprocessing.messageserver.socket;

import dto.ParentDTO;
import messageV2.Message;

public interface MessageProcessor {
    Message<? extends ParentDTO> pool();

    void send(Message<? extends ParentDTO> message);

    Message<? extends ParentDTO> take();

    void close();
}
