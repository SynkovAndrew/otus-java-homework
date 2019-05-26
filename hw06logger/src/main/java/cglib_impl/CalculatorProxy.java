package cglib_impl;

import common.Log;
import common.LogUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class CalculatorProxy {
    private final Map<String, Boolean> reflectionAnalysis;
    private final CalculatorWithoutI calculator;

    private CalculatorProxy() {
        this.reflectionAnalysis = new HashMap<>();
        final MethodInterceptor handler = (obj, method, args, proxy) -> {
            final boolean isLogged = ofNullable(reflectionAnalysis.get(method.getName()))
                    .orElseGet(() -> {
                        boolean result = method.isAnnotationPresent(Log.class);
                        reflectionAnalysis.put(method.getName(), result);
                        return result;
                    });
            if (isLogged) {
                System.out.println(LogUtils.createMessage(method, Arrays.asList(args)));
            }
            return method.invoke(new CalculatorWithoutI(), args);
        };
        this.calculator = (CalculatorWithoutI) Enhancer.create(CalculatorWithoutI.class, handler);
    }

    public static CalculatorWithoutI get() {
        final CalculatorProxy calculatorProxy = new CalculatorProxy();
        return calculatorProxy.getCalculator();
    }

    private CalculatorWithoutI getCalculator() {
        return calculator;
    }
}
