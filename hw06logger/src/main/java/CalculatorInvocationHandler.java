import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class CalculatorInvocationHandler implements InvocationHandler {
    private final ICalculator calculator;

    public CalculatorInvocationHandler(final ICalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {
        System.out.println(createMessage(method, Arrays.asList(args)));
        return method.invoke(calculator, args);
    }

    private String createMessage(final Method method, final List<Object> args) {
        return "Executed method: " + method.getName() + " ( " +
                args.stream()
                        .map(arg -> "param" + args.indexOf(arg) + ": " + arg)
                        .collect(joining(", ")) + " )";
    }
}
