package messageV2;

import com.google.gson.Gson;
import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.ParentDTO;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringBetween;

class MessageMappingService {
    private static final Map<String, Class<? extends ParentDTO>> classes = Map.of(
            "CreateUserRequestDTO", CreateUserRequestDTO.class,
            "FindUsersResponseDTO", FindUsersResponseDTO.class
    );
    private static final Gson gson = new Gson();

    static String mapToJson(final Message<? extends ParentDTO> message) {
        return gson.toJson(message);
    }

    static Message<? extends ParentDTO> mapToObject(final String json) {
        final var className = substringBetween(json, "\"className\":\"", "\",\"content");
        final var content = substringBetween(json, "\"content\":", ",\"instanceId");
        final var instanceId = substringBetween(json, "\"instanceId\":\"", "\"");

        final Class<? extends ParentDTO> clazz = classes.get(className);
        final ParentDTO object = gson.fromJson(content, clazz);
        return new Message<>(className, object, instanceId);
    }
}
