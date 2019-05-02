import common.ProxyUtils;

public class Main {
    public static void main(String[] args) {
        // jdk proxy
        final var iCalculator = ProxyUtils.createProxyUsingJDK();

        iCalculator.multiplyWithTwo(2);
        iCalculator.add(1, 3);
        iCalculator.multiplyWithThree(3);
        iCalculator.addThreeIntegers(4, 5, 7);

        // cglib proxy
        final var calculator = ProxyUtils.createProxyUsingCglib();

        calculator.multiplyWithTwo(2);
        calculator.add(1, 3);
        calculator.multiplyWithThree(3);
        calculator.addThreeIntegers(4, 5, 7);
    }
}
