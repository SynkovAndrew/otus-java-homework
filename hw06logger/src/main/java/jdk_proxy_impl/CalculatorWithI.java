package jdk_proxy_impl;

import common.Log;

public class CalculatorWithI implements ICalculator {
    @Log
    @Override
    public int add(final int a, final int b) {
        return a + b;
    }

    @Log
    @Override
    public int multiplyWithTwo(final int a) {
        return a * 2;
    }

    public int multiplyWithThree(final int a) {
        return a * 3;
    }

    @Log
    @Override
    public int addThreeIntegers(final int a, final int b, final int c) {
        return a + b + c;
    }
}
