package reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionUtils {
    public static <T> T createObject(final Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static String getFieldName(final Field field) {
        field.setAccessible(true);
        return field.getName();
    }

    public static void setFieldValue(final Field field, final Object object, final Object value) {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Optional<Object> getFieldValue(final Field field, final Object object) {
        field.setAccessible(true);
        try {
            return Optional.ofNullable(field.get(object));
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Field> getFieldAnnotatedWith(final Class clazz, final Class annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .findFirst();
    }
}
