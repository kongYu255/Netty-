package com.yuy.netty.util;

public class IdUtils {

    private static final SnowFlake snowFlake = new SnowFlake(0 ,0);

    public static String getId() {
        return String.valueOf(snowFlake.nextId());
    }
}
