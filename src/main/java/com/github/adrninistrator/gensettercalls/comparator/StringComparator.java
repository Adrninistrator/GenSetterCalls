package com.github.adrninistrator.gensettercalls.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

    private static volatile StringComparator instance;

    private StringComparator() {
    }

    public static StringComparator getInstance() {

        if (instance == null) {
            synchronized (StringComparator.class) {
                if (instance == null) {
                    instance = new StringComparator();
                }
            }
        }
        return instance;
    }

    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
