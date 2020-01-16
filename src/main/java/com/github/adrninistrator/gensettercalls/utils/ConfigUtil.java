package com.github.adrninistrator.gensettercalls.utils;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {

    public static final String ARG_DEBUG = "debug";
    public static final String ARG_SORT = "sort";
    public static final String ARG_HIDETYPE = "hideType";
    public static final String ARG_HIDEIMPORT = "hideImport";
    public static final String ARG_NONCUSTOMPACKAGE = "nonCustomPackage";

    private static boolean debug;

    private static boolean sortFieldByName;

    private static boolean hideType;

    private static boolean hideImport;

    private static List<String> nonCustomPackageList;

    static {

        PrintUtil.infoPrint("Bug report: https://github.com/Adrninistrator/GenSetterCalls");

        init();
    }

    public static void init() {

        String debugArg = System.getProperty(ARG_DEBUG);
        debug = (null != debugArg && !debugArg.isEmpty());

        String sortArg = System.getProperty(ARG_SORT);
        sortFieldByName = (null != sortArg && !sortArg.isEmpty());

        String hideTypeArg = System.getProperty(ARG_HIDETYPE);
        hideType = (null != hideTypeArg && !hideTypeArg.isEmpty());

        String hideImportArg = System.getProperty(ARG_HIDEIMPORT);
        hideImport = (null != hideImportArg && !hideImportArg.isEmpty());

        String nonCustomPackageArg = System.getProperty(ARG_NONCUSTOMPACKAGE);
        if (null != nonCustomPackageArg && !nonCustomPackageArg.isEmpty()) {

            String[] tmpArray = nonCustomPackageArg.split(",");

            int length = tmpArray.length;

            nonCustomPackageList = new ArrayList<String>(length);

            for (String tmpString : tmpArray) {

                String str = tmpString.trim();

                if (!str.isEmpty() && !nonCustomPackageList.contains(str)) {
                    nonCustomPackageList.add(str);
                }
            }
        } else {
            nonCustomPackageList = new ArrayList<String>(0);
        }

        PrintUtil.infoPrint("[debug]: " + debug + ", [sort]: " + sortFieldByName + ", [hideType]: " + hideType + ", [hideImport]: " + hideImport +
                ", [nonCustomPackage]: " + nonCustomPackageList);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isSortFieldByName() {
        return sortFieldByName;
    }

    public static boolean isHideType() {
        return hideType;
    }

    public static boolean isHideImport() {
        return hideImport;
    }

    public static List<String> getNonCustomPackageList() {
        return nonCustomPackageList;
    }

    private ConfigUtil() {
        throw new IllegalStateException("illegal");
    }
}
