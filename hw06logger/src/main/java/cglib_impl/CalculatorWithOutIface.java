package cglib_impl;

import common.Log;

public class CalculatorWithOutIface {
    @Log
    public int add(final int a, final int b) {
        return a + b;
    }

    @Log
    public int multiplyWithTwo(final int a) {
        return a * 2;
    }

    @Log
    public int addThreeIntegers(final int a, final int b, final int c) {
        return a + b + c;
    }
}
