package ru.otus.homework.utils;

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
            return Optional.ofNullable(field.get(o));
        } catch (IllegalAccessException ignored) {
            return Optional.empty();
        }
    }

    public static boolean isReflectedAsNumberOrBoolean(final Class<?> type) {
        return Number.class.isAssignableFrom(type) ||
                (NUMBER_AND_BOOLEAN_TYPES.contains(type) || NUMBER_AND_BOOLEAN_PRIMITIVE_TYPES.contains(type));
    }

    public static boolean isReflectedAsString(final Class<?> type) {
        return String.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type);
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
        final var isCollection = Collection.class.isAssignableFrom(type);
        final Type[] actualTypeArguments = isCollection ?
                ((ParameterizedType) genericType).getActualTypeArguments() : new Type[] {null};
        return Collection.class.isAssignableFrom(type) &&
                NUMBER_AND_BOOLEAN_TYPES.contains(actualTypeArguments[0]);
    }

    public static boolean isReflectedAsStringCollection(final Class<?> type, final Type genericType) {
        final var isCollection = Collection.class.isAssignableFrom(type);
        final Type[] actualTypeArguments = isCollection ?
                ((ParameterizedType) genericType).getActualTypeArguments() : new Type[] {null};
        return Collection.class.isAssignableFrom(type) && actualTypeArguments[0] == String.class;
    }

    public static boolean isReflectedAsNumberOrBooleanCollection(final Object object) {
        final var type = object.getClass();
        final var isCollection = Collection.class.isAssignableFrom(type);
        final var genericType = isCollection ?
                ((Collection) object).stream().findFirst().map(Object::getClass).orElse(null) : null;
        return isCollection && NUMBER_AND_BOOLEAN_TYPES.contains(genericType);
    }

    public static boolean isReflectedAsStringCollection(final Object object) {
        final var type = object.getClass();
        final var isCollection = Collection.class.isAssignableFrom(type);
        final var genericType = isCollection ?
                ((Collection) object).stream().findFirst().map(Object::getClass).orElse(null) : null;
        return Collection.class.isAssignableFrom(type) && genericType == String.class;
    }
}
