package com.github.adrninistrator.gensettercalls.utils;

public class PrintUtil {

    public static void debugPrint(String str) {
        if (ConfigUtil.isDebug()) {
            System.out.println(str);
        }
    }

    public static void infoPrint(String str) {
        System.out.println(str);
    }

    private PrintUtil() {
        throw new IllegalStateException("illegal");
    }
}
