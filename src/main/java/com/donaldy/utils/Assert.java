package com.donaldy.utils;

import com.donaldy.handler.RestfulException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

public class Assert {
    public Assert() {
    }

    public static void isTrue(boolean expression, int code, String message) {
        if (!expression) {
            throw new RestfulException(code, message);
        }
    }

    public static void isFalse(boolean expression, int code, String message) {
        if (expression) {
            throw new RestfulException(code, message);
        }
    }

    public static void isNull(Object object, int code, String message) {
        if (object != null) {
            throw new RestfulException(code, message);
        }
    }

    public static void notNull(Object object, int code, String message) {
        if (object == null) {
            throw new RestfulException(code, message);
        }
    }

    public static void hasText(String text, int code, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new RestfulException(code, message);
        }
    }

    public static void notEmpty(Map<?, ?> map, int code, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new RestfulException(code, message);
        }
    }
}
