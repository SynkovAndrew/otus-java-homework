package ru.otus.homework.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class SerializationUtils {
    public static String toJson(final Object object) {
        return ofNullable(object)
                .map(obj -> ofNullable(objectToJson(obj))
                        .orElseGet(() -> complexObjectToJson(object)))
                .orElse("null");
    }

    private static String complexObjectToJson(final Object object) {
        final var clazz = object.getClass();
        final var fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) &&
                        !Modifier.isTransient(field.getModifiers()))
                .map(field -> fieldToJson(field, object))
                .filter(Objects::nonNull)
                .collect(joining(","));
        return "{" + fields + "}";
    }

    private static String objectToJson(final Object object) {
        final var clazz = object.getClass();
        if (ReflectionUtils.isReflectedAsNumberOrBoolean(clazz)) {
            return "" + object;
        } else if (ReflectionUtils.isReflectedAsString(clazz)) {
            return "\"" + object + "\"";
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanArray(clazz)) {
            return "[" + getNumberArrayValues(object) + "]";
        } else if (ReflectionUtils.isReflectedAsStringArray(clazz)) {
            return "[" + getStringArrayValues(object) + "]";
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanCollection(object)) {
            return "[" + getNumberCollectionValues(object) + "]";
        } else if (ReflectionUtils.isReflectedAsStringCollection(object)) {
            return "[" + getStringCollectionValues(object) + "]";
        }
        return null;
    }

    private static String fieldToJson(final Field field, final Object object) {
        if (ReflectionUtils.isReflectedAsNumberOrBoolean(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + value)
                    .orElse(null);
        } else if (ReflectionUtils.isReflectedAsString(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "\"" + value + "\"")
                    .orElse(null);
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanArray(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getNumberArrayValues(value) + "]")
                    .orElse(null);
        } else if (ReflectionUtils.isReflectedAsStringArray(field.getType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getStringArrayValues(value) + "]")
                    .orElse(null);
        } else if (ReflectionUtils.isReflectedAsNumberOrBooleanCollection(field.getType(), field.getGenericType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getNumberCollectionValues(value) + "]")
                    .orElse(null);
        } else if (ReflectionUtils.isReflectedAsStringCollection(field.getType(), field.getGenericType())) {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + "[" + getStringCollectionValues(value) + "]")
                    .orElse(null);
        } else {
            return ReflectionUtils.getFieldValue(field, object)
                    .map(value -> "\"" + field.getName() + "\":" + complexObjectToJson(value))
                    .orElse(null);
        }
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
