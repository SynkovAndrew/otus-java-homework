package common;

import cglib_impl.CalculatorWithOutIface;
import jdk_proxy_impl.CalculatorWithIface;
import jdk_proxy_impl.ICalculator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyUtils {
    public static ICalculator createProxyUsingJDK() {
        return (ICalculator) Proxy.newProxyInstance(
                ICalculator.class.getClassLoader(),
                CalculatorWithIface.class.getInterfaces(),
                (proxy, method, arguments) -> {
                    System.out.println(LogUtils.createMessage(method, Arrays.asList(arguments)));
                    return method.invoke(new CalculatorWithIface(), arguments);
                }
        );
    }

    public static CalculatorWithOutIface createProxyUsingCglib() {
        MethodInterceptor handler = (obj, method, args, proxy) -> {
            System.out.println(LogUtils.createMessage(method, Arrays.asList(args)));
            return method.invoke(new CalculatorWithOutIface(), args);
        };

        return (CalculatorWithOutIface) Enhancer.create(CalculatorWithOutIface.class, handler);
    }
}
