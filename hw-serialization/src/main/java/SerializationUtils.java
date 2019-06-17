import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SerializationUtils {


    public static String toJson(final Object object) {
        Class clazz = object.getClass();

        final var fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isTransient(field.getModifiers()))
                .map(field -> fieldToJson(field, object))
                .collect(Collectors.joining(","));

        return "{" + fields + "}";
    }

    private static String fieldToJson(final Field field, final Object object) {
        if (ReflectionUtils.isReflectedAsNumber(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + value)
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsString(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "\"" + value + "\"")
                    .orElse("");
        }

        return "";
    }
}
