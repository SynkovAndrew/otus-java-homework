import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        final var iCalculator = (ICalculator) Proxy.newProxyInstance(ICalculator.class.getClassLoader(),
                new Class[]{ICalculator.class},
                new CalculatorInvocationHandler(new Calculator())
        );

        iCalculator.multiplyWithTwo(2);
        iCalculator.add(1, 3);
        iCalculator.addThreeIntegers(4, 5, 7);
    }
}
