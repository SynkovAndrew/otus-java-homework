package jdbc;

import annotation.Id;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static reflection.ReflectionUtils.getFieldAnnotatedWith;
import static reflection.ReflectionUtils.getFieldName;

public class SQLCacheUtils {
    private final String INSERT_ONE_STATEMENT = "insert into %s (%s) values (%s)";
    private final String SELECT_ALL_STATEMENT = "select * from %s";
    private final String SELECT_1_STATEMENT = "select 1 from %s where %s = ?";
    private final String SELECT_ONE_BY_ID_STATEMENT = "select * from %s where %s = ?";
    private final String UPDATE_ONE_BY_ID_STATEMENT = "update %s set %s where %s = ?";

    private Map<String, List<Field>> fieldMap;
    private Map<String, Field> idFieldMap;
    private Map<String, String> insertStatementMap;
    private Map<String, String> updateStatementMap;
    private Map<String, String> selectOneStatementMap;
    private Map<String, String> checkIfExistsStatementMap;

    public SQLCacheUtils() {
        fieldMap = new HashMap<>();
        idFieldMap = new HashMap<>();
        insertStatementMap = new HashMap<>();
        updateStatementMap = new HashMap<>();
        selectOneStatementMap = new HashMap<>();
        checkIfExistsStatementMap = new HashMap<>();
    }

    String getSelectAllStatement(final Class clazz) {
        return String.format(SELECT_ALL_STATEMENT, clazz.getSimpleName());
    }

    String getCheckIfExistsStatement(final Class clazz) {
        final String name = clazz.getSimpleName();
        return ofNullable(checkIfExistsStatementMap.get(name))
                .orElseGet(() -> {
                    final String idFieldName = getIdField(clazz).map(Field::getName).get();
                    final var checkIfExistsStatement = String.format(SELECT_1_STATEMENT, clazz.getSimpleName(), idFieldName);
                    checkIfExistsStatementMap.put(name, checkIfExistsStatement);
                    return checkIfExistsStatement;
                });
    }

    String getSelectOneStatement(final Class clazz) {
        final String name = clazz.getSimpleName();
        return ofNullable(selectOneStatementMap.get(name))
                .orElseGet(() -> {
                    final String idFieldName = getIdField(clazz).map(Field::getName).get();
                    final var insertStatement = String.format(SELECT_ONE_BY_ID_STATEMENT, name, idFieldName);
                    selectOneStatementMap.put(name, insertStatement);
                    return insertStatement;
                });
    }

    String getInsertStatement(final Class clazz) {
        final String name = clazz.getSimpleName();
        return ofNullable(insertStatementMap.get(name))
                .orElseGet(() -> {
                    final String fieldNames = getFields(clazz).stream()
                            .filter(field -> !field.isAnnotationPresent(Id.class))
                            .map(Field::getName)
                            .collect(Collectors.joining(", "));
                    final String fieldValues = getFields(clazz).stream()
                            .filter(field -> !field.isAnnotationPresent(Id.class))
                            .map(field -> "?")
                            .collect(Collectors.joining(", "));
                    final var insertStatement = String.format(INSERT_ONE_STATEMENT, name, fieldNames, fieldValues);
                    insertStatementMap.put(name, insertStatement);
                    return insertStatement;
                });
    }

    String getUpdateStatement(final Class clazz) {
        final String name = clazz.getSimpleName();
        return ofNullable(updateStatementMap.get(name))
                .orElseGet(() -> {
                    final String idFieldName = getIdField(clazz).map(Field::getName).get();
                    final String matches = getFields(clazz).stream()
                            .filter(field -> !field.isAnnotationPresent(Id.class))
                            .map(field -> getFieldName(field) + " = ?")
                            .collect(joining(","));
                    final var updateStatement = String.format(UPDATE_ONE_BY_ID_STATEMENT, name, matches, idFieldName);
                    updateStatementMap.put(name, updateStatement);
                    return updateStatement;
                });
    }

    List<Field> getFields(final Class clazz) {
        final var name = clazz.getSimpleName();
        return ofNullable(fieldMap.get(name))
                .orElseGet(() -> {
                    final var fields = Arrays.asList(clazz.getDeclaredFields());
                    fieldMap.put(name, fields);
                    return fields;
                });
    }

    Optional<Field> getIdField(final Class clazz) {
        final var name = clazz.getSimpleName();
        return ofNullable(idFieldMap.get(name))
                .or(() -> getFieldAnnotatedWith(clazz, Id.class)
                        .map(field -> {
                            idFieldMap.put(name, field);
                            return Optional.of(field);
                        })
                        .orElse(Optional.empty()));
    }
}
