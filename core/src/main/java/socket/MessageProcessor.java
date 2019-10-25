package socket;

import dto.ParentDTO;
import instance.Instance;
import messageV2.Message;

import java.io.IOException;

public interface MessageProcessor extends Instance {
    void close() throws IOException;

    MessageProcessorType getType();

    Message<? extends ParentDTO> pool();

    void put(Message<? extends ParentDTO> message);
}
