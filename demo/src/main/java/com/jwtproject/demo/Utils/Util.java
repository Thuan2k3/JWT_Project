package com.jwtproject.demo.Utils;

import java.util.Objects;

public class Util {
    public static boolean notNull(Object object) {
        return ! Objects.isNull(object);
    }
}
