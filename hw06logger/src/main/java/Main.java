public class Main {
    public static void main(String[] args) {
        final var iCalculator = jdk_proxy_impl.CalculatorProxy.get();

        iCalculator.multiplyWithTwo(2);
        iCalculator.add(1, 3);
        iCalculator.add(1, 4);
        iCalculator.multiplyWithThree(3);
        iCalculator.addThreeIntegers(4, 5, 7);

        final var calculator = cglib_impl.CalculatorProxy.get();

        calculator.multiplyWithTwo(2);
        calculator.add(1, 3);
        calculator.add(1, 4);
        calculator.multiplyWithThree(3);
        calculator.addThreeIntegers(4, 5, 7);
    }
}
