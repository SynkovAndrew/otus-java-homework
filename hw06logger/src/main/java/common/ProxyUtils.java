package common;

import cglib_impl.CalculatorWithoutI;
import jdk_proxy_impl.CalculatorWithI;
import jdk_proxy_impl.ICalculator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyUtils {
    public static ICalculator createProxyUsingJDK() {
        return (ICalculator) Proxy.newProxyInstance(
                ICalculator.class.getClassLoader(),
                CalculatorWithI.class.getInterfaces(),
                (proxy, method, arguments) -> {
                    if (ReflectionUtils.isMethodPresentedAndAnnotatedWith(CalculatorWithI.class, method, Log.class)) {
                        System.out.println(LogUtils.createMessage(method, Arrays.asList(arguments)));
                    }
                    return method.invoke(new CalculatorWithI(), arguments);
                }
        );
    }

    public static CalculatorWithoutI createProxyUsingCglib() {
        final MethodInterceptor handler = (obj, method, args, proxy) -> {
            if (method.isAnnotationPresent(Log.class)) {
                System.out.println(LogUtils.createMessage(method, Arrays.asList(args)));
            }
            return method.invoke(new CalculatorWithoutI(), args);
        };

        return (CalculatorWithoutI) Enhancer.create(CalculatorWithoutI.class, handler);
    }
}
