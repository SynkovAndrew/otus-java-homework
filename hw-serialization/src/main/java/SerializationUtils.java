import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.joining;

public class SerializationUtils {
    public static String toJson(final Object object) {
        final var clazz = object.getClass();

        final var fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isTransient(field.getModifiers()))
                .map(field -> fieldToJson(field, object))
                .collect(joining(","));

        return "{" + fields + "}";
    }

    private static String fieldToJson(final Field field, final Object object) {
        if (ReflectionUtils.isReflectedAsNumberOrBoolean(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + value)
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsString(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "\"" + value + "\"")
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanArray(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getNumberArrayValues(value) + "]")
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsStringArray(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getStringArrayValues(value) + "]")
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanCollection(field.getType(), field.getGenericType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getNumberCollectionValues(value) + "]")
                    .orElse("");
        } else if (ReflectionUtils.isReflectedAsStringCollection(field.getType(), field.getGenericType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getStringCollectionValues(value) + "]")
                    .orElse("");
        }

        return "";
    }

    private static String getNumberArrayValues(Object array) {
        final var values = new StringBuilder();
        for (int i = 0; i < Array.getLength(array); i++) {
            values.append(Array.get(array, i));
            if (i != Array.getLength(array) - 1) {
                values.append(",");
            }
        }
        return values.toString();
    }

    private static String getNumberCollectionValues(final Object collection) {
        return ((Collection<?>) collection).stream()
                .map(Object::toString)
                .collect(joining(","));
    }

    private static String getStringCollectionValues(final Object collection) {
        return ((Collection<?>) collection).stream()
                .map(Object::toString)
                .map(val -> "\"" + val + "\"")
                .collect(joining(","));
    }

    private static String getStringArrayValues(Object array) {
        final var values = new StringBuilder();
        for (int i = 0; i < Array.getLength(array); i++) {
            values.append("\"");
            values.append(Array.get(array, i));
            values.append("\"");
            if (i != Array.getLength(array) - 1) {
                values.append(",");
            }
        }
        return values.toString();
    }
}
