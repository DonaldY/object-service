package com.donaldy.utils;

public class NumberUtils {

    public static boolean isEmpty(Integer arg) {
        if (null == arg || arg == 0) {
            return true;
        }
        return false;
    }

    public static boolean isPositive(Integer arg) {
        if (null != arg && arg > 0) {
            return true;
        }
        return false;
    }
}