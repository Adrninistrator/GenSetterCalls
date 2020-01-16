package com.github.adrninistrator.gensettercalls.utils;

import java.io.File;

public class FileUtil {

    public static boolean isJavaOrClassFile(String filePath) {
        String filePathLower = filePath.toLowerCase();
        return filePathLower.endsWith(".java") || filePathLower.endsWith(".class");
    }

    public static String[] splitFilePath(String filePath) {

        String regex;

        if ("\\".equals(File.separator)) {
            regex = "\\\\";
        } else {
            regex = File.separator;
        }

        return filePath.split(regex);
    }

    private FileUtil() {
        throw new IllegalStateException("illegal");
    }
}
