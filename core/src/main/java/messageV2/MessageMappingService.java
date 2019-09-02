package messageV2;

import com.google.gson.Gson;
import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.ParentDTO;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class MessageMappingService {
    private static final Gson gson = new Gson();
    private static final Map<String, Class<? extends ParentDTO>> classes = Map.of(
            "CreateUserRequestDTO", CreateUserRequestDTO.class,
            "FindUsersResponseDTO", FindUsersResponseDTO.class
    );

    public static Message<? extends ParentDTO> mapToObject(final String json) {
        final var className = remove(substringBetween(json, "className:", ","), "\"");
        final Class<? extends ParentDTO> clazz = classes.get(className);
        final ParentDTO object = gson.fromJson(json, clazz);
        return new Message<>(object, className);
    }

    public static String mapToJson(final Message<? extends ParentDTO> message) {
        return gson.toJson(message);
    }
}
