package common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static boolean isMethodPresentedAndAnnotatedWith(final Class<?> clazz,
                                                            final Method method,
                                                            final Class<? extends Annotation> annotationClass) {
        final var methodName = method.getName();
        final var parameterTypes = method.getParameterTypes();
        try {
            final var methodOfClass = clazz.getMethod(methodName, parameterTypes);
            return methodOfClass.isAnnotationPresent(annotationClass);
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }
}
