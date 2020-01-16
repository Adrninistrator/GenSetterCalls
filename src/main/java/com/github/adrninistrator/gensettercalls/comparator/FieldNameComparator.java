package com.github.adrninistrator.gensettercalls.comparator;

import java.lang.reflect.Field;
import java.util.Comparator;

public class FieldNameComparator implements Comparator<Field> {

    private static volatile FieldNameComparator instance;

    private FieldNameComparator() {
    }

    public static FieldNameComparator getInstance() {

        if (instance == null) {
            synchronized (FieldNameComparator.class) {
                if (instance == null) {
                    instance = new FieldNameComparator();
                }
            }
        }
        return instance;
    }

    @Override
    public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
