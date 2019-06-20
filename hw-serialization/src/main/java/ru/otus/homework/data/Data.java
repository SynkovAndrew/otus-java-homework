package ru.otus.homework.data;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class Data {
    private final static int staticInteger = 10;
    private final static String staticString = "staticString";
    private final transient String transientString = "transientString";
    private final int primitiveInteger = 20;
    private final String string = "string";
    private final long primitiveLongNumber = 30L;
    private final Integer integer = 100;
    private final Long longNumber = 200L;
    private final int[] primitiveIntArray = new int[]{1, 2, 3, 4, 5};
    private final Integer[] intArray = new Integer[]{10, 20, 30, 40, 50};
    private final short[] shortArray = new short[]{1, 2, 3, 4, 5};
    private final String[] stringArray = new String[]{"abc", "cvf", "asd", "qwe"};
    private final char[] charArray = new char[]{'a', 'b', 'x', 'g'};
    private final boolean[] primitiveBooleanArray = new boolean[]{true, false, true};
    private final Boolean[] booleanArray = new Boolean[]{true, false, true};
    private final byte[] byteArray = new byte[]{'3', '4', 1, 5, 90};
    private final Set<String> stringSet = newHashSet("lsdf", ";lk;sasad", "sdfavcx");
    private final List<Double> doubleList = newArrayList(1.22, 3.55, 2.33);
    private final Object nullObj = null;
    private final InnerData innerData = new InnerData();
}
