package com.donaldy.utils;

public class NumberUtils {

    public static boolean isEmpty(Integer arg) {
        return null == arg || arg == 0;
    }

    public static boolean isPositive(Integer arg) {
        return null != arg && arg > 0;
    }
}