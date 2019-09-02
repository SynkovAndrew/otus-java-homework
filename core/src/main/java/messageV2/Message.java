package messageV2;

import dto.ParentDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class Message<T extends ParentDTO> {
    private final T content;
}
