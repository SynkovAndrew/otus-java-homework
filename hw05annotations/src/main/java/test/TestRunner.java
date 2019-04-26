package test;

import java.lang.reflect.Method;

import static java.util.Optional.ofNullable;

public class TestRunner {
    private final TestRunnerContext context;
    private final Class testClass;

    private TestRunner(Class clazz) {
        testClass = clazz;
        context = TestRunnerContext.parseTestClass(clazz);
    }

    public static void runTests(Class clazz) {
        TestRunner runner = new TestRunner(clazz);
        runner.process();
    }

    private void process() {
        try {
            for (Method method : context.getBeforeAllMethods()) {
                ReflectionUtils.invoke(method, null);
            }

            for (Method method : context.getTestMethods()) {
                ofNullable(ReflectionUtils.newInstance(testClass))
                        .ifPresent(inst -> runTestMethod(method, inst));
            }
        } catch (Exception e) {
            System.out.println("Test hasn't passed!");
        } finally {
            try {
                System.out.println("\n");
                for (Method method : context.getAfterAllMethods()) {
                    ReflectionUtils.invoke(method, null);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void runTestMethod(final Method method, final Object instance) {
        System.out.println("\nNew test instance's been created: " + instance.hashCode() + "\n");

        try {
            for (Method beforeEachMethod : context.getBeforeEachMethods()) {
                ReflectionUtils.invoke(beforeEachMethod, instance);
            }

            ReflectionUtils.invoke(method, instance);
            System.out.println("Test has passed!");
        } catch (Exception e) {
            System.out.println("Test hasn't passed!");
        } finally {
            try {
                for (Method afterEachMethod : context.getAfterEachMethods()) {
                    ReflectionUtils.invoke(afterEachMethod, instance);
                }
            } catch (Exception ignored) {

            }
        }
    }
}
