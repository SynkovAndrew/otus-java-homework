package test;

import annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

class TestRunnerContext {
    private final List<Method> beforeAllMethods;
    private final List<Method> afterAllMethods;
    private final List<Method> beforeEachMethods;
    private final List<Method> afterEachMethods;
    private final List<Method> testMethods;

    private TestRunnerContext() {
        beforeAllMethods = new ArrayList<>();
        afterAllMethods = new ArrayList<>();
        beforeEachMethods = new ArrayList<>();
        afterEachMethods = new ArrayList<>();
        testMethods = new ArrayList<>();
    }

    static TestRunnerContext parseTestClass(Class clazz) {
        TestRunnerContext context = new TestRunnerContext();
        context.parse(clazz);
        context.sort();
        return context;
    }

    private void parse(Class clazz) {
        ofNullable(Arrays.asList(clazz.getMethods())).orElse(emptyList())
                .forEach(method -> {
                    if (method.isAnnotationPresent(CustomBeforeAll.class)) {
                        beforeAllMethods.add(method);
                    } else if (method.isAnnotationPresent(CustomAfterAll.class)) {
                        afterAllMethods.add(method);
                    } else if (method.isAnnotationPresent(CustomBeforeEach.class)) {
                        beforeEachMethods.add(method);
                    } else if (method.isAnnotationPresent(CustomAfterEach.class)) {
                        afterEachMethods.add(method);
                    } else if (method.isAnnotationPresent(CustomTest.class)) {
                        testMethods.add(method);
                    }
                });
    }

    private void sort() {
        beforeAllMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(CustomBeforeAll.class).order()));
        afterAllMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(CustomAfterAll.class).order()));
        beforeEachMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(CustomBeforeEach.class).order()));
        afterEachMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(CustomAfterEach.class).order()));
    }

    List<Method> getBeforeAllMethods() {
        return beforeAllMethods;
    }

    List<Method> getAfterAllMethods() {
        return afterAllMethods;
    }

    List<Method> getBeforeEachMethods() {
        return beforeEachMethods;
    }

    List<Method> getAfterEachMethods() {
        return afterEachMethods;
    }

    List<Method> getTestMethods() {
        return testMethods;
    }
}
