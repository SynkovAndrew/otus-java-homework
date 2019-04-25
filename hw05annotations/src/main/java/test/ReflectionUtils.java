package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectionUtils {
    static Object newInstance(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }

    static Object invoke(Method method, Object object) throws Exception {
        return method.invoke(object);
    }
}
