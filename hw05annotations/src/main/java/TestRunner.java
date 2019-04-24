import annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class TestRunner {
    private static final List<Method> beforeAllMethods = new ArrayList<>();
    private static final List<Method> afterAllMethods = new ArrayList<>();
    private static final List<Method> beforeEachMethods = new ArrayList<>();
    private static final List<Method> afterEachMethods = new ArrayList<>();
    private static final List<Method> testMethods = new ArrayList<>();

    public static void run(Class clazz) {
        prepare(clazz);
        process(clazz);
    }

    private static void process(Class clazz) {
        beforeAllMethods.stream()
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(CustomBeforeAll.class).order()))
                .forEach(method -> invoke(method, null));

        testMethods.forEach(method ->
                ofNullable(newInstance(clazz))
                        .ifPresent(inst -> {
                            System.out.println("\nNew test instance's been created: " + inst.hashCode() + "\n");
                            beforeEachMethods.stream()
                                    .sorted(Comparator.comparingInt(m -> m.getAnnotation(CustomBeforeEach.class).order()))
                                    .forEach(beforeEachMethod -> invoke(beforeEachMethod, inst));
                            invoke(method, inst);
                            afterEachMethods.stream()
                                    .sorted(Comparator.comparingInt(m -> m.getAnnotation(CustomAfterEach.class).order()))
                                    .forEach(beforeEachMethod -> invoke(beforeEachMethod, inst));
                        }));

        System.out.println("\n");
        afterAllMethods.stream()
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(CustomAfterAll.class).order()))
                .forEach(method -> invoke(method, null));
    }

    private static void prepare(Class clazz) {
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

    private static Object newInstance(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }

    private static Object invoke(Method method, Object object) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
