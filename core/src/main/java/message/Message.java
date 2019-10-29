package message;

import lombok.*;

@RequiredArgsConstructor
@Getter
public class Message<T> {
    private final T content;
    private final Queue targetQueue;
}
