import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ReflectionUtils {
    private final static Set<Class<?>> NUMBER_REFLECTED_PRIMITIVES = newHashSet(
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class
    );

    public static Optional<Object> getFieldValue(final Field field, final Object o) {
        field.setAccessible(true);
        try {
            return Optional.of(field.get(o));
        } catch (IllegalAccessException ignored) {
            return Optional.empty();
        }
    }

    public static boolean isReflectedAsNumber(final Class<?> type) {
        return Number.class.isAssignableFrom(type) || NUMBER_REFLECTED_PRIMITIVES.contains(type);
    }

    public static boolean isReflectedAsString(final Class<?> type) {
        return String.class.isAssignableFrom(type);
    }
}
