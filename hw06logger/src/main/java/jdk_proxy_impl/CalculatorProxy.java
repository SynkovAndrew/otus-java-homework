package jdk_proxy_impl;

import common.Log;
import common.LogUtils;
import common.ReflectionUtils;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class CalculatorProxy {
    private final Map<String, Boolean> reflectionAnalysis;
    private final ICalculator calculator;

    private CalculatorProxy() {
        this.reflectionAnalysis = new HashMap<>();
        this.calculator = (ICalculator) Proxy.newProxyInstance(
                ICalculator.class.getClassLoader(),
                CalculatorWithI.class.getInterfaces(),
                (proxy, method, arguments) -> {
                    final boolean isLogged = ofNullable(reflectionAnalysis.get(method.getName()))
                            .orElseGet(() -> {
                                boolean result = ReflectionUtils.isMethodPresentedAndAnnotatedWith(
                                        CalculatorWithI.class, method, Log.class);
                                reflectionAnalysis.put(method.getName(), result);
                                return result;
                            });
                    if (isLogged) {
                        System.out.println(LogUtils.createMessage(method, Arrays.asList(arguments)));
                    }
                    return method.invoke(new CalculatorWithI(), arguments);
                }
        );
    }

    public static ICalculator get() {
        final CalculatorProxy calculatorProxy = new CalculatorProxy();
        return calculatorProxy.getCalculator();
    }

    private ICalculator getCalculator() {
        return calculator;
    }
}
