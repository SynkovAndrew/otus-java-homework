package reflection;

import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtils {

    public static String getFieldName(final Field field) {
        field.setAccessible(true);
        return field.getName();
    }

    public static Optional<Object> getFieldValue(final Field field, final Object object) {
        field.setAccessible(true);
        try {
            return Optional.of(field.get(object));
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }
}
