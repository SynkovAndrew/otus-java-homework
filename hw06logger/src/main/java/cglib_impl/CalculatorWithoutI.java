package cglib_impl;

import common.Log;

public class CalculatorWithoutI {
    @Log
    public int add(final int a, final int b) {
        return a + b;
    }

    @Log
    public int multiplyWithTwo(final int a) {
        return a * 2;
    }

    public int multiplyWithThree(final int a) {
        return a * 3;
    }

    @Log
    public int addThreeIntegers(final int a, final int b, final int c) {
        return a + b + c;
    }
}
