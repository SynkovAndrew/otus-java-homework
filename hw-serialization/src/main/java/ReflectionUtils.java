import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ReflectionUtils {
    private final static Set<Class<?>> NUMBER_AND_BOOLEAN_PRIMITIVE_TYPES = newHashSet(
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class,
            boolean.class
    );

    private final static Set<Class<?>> NUMBER_AND_BOOLEAN_TYPES = newHashSet(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class
    );

    public static Optional<Object> getFieldValue(final Field field, final Object o) {
        field.setAccessible(true);
        try {
            return Optional.of(field.get(o));
        } catch (IllegalAccessException ignored) {
            return Optional.empty();
        }
    }

    public static boolean isReflectedAsNumberOrBoolean(final Class<?> type) {
        return Number.class.isAssignableFrom(type) ||
                (NUMBER_AND_BOOLEAN_TYPES.contains(type) || NUMBER_AND_BOOLEAN_PRIMITIVE_TYPES.contains(type));
    }

    public static boolean isReflectedAsString(final Class<?> type) {
        return String.class.isAssignableFrom(type);
    }

    public static boolean isReflectedAsNumberOrBooleanArray(final Class<?> type) {
        return type.isArray() && (NUMBER_AND_BOOLEAN_PRIMITIVE_TYPES.contains(type.getComponentType()) ||
                NUMBER_AND_BOOLEAN_TYPES.contains(type.getComponentType()));
    }

    public static boolean isReflectedAsStringArray(final Class<?> type) {
        return type.isArray() && (type.getComponentType() == String.class ||
                type.getComponentType() == char.class);
    }

    public static boolean isReflectedAsNumberOrBooleanCollection(final Class<?> type, final Type genericType) {
        return Collection.class.isAssignableFrom(type) &&
                NUMBER_AND_BOOLEAN_TYPES.contains((Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0]);
    }

    public static boolean isReflectedAsStringCollection(final Class<?> type, final Class<?> genericType) {
        return Collection.class.isAssignableFrom(type) && genericType == String.class;
    }
}
