package com.github.adrninistrator.gensettercalls.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {

    private static final String[] NON_CUSTOM_PACKAGES = {"java.", "javax.", "javafx.", "com.sun", "sun."};

    public static boolean isCustomType(Class clazz) {

        if (clazz.isPrimitive() || clazz.isArray() || clazz.isEnum()) {
            return false;
        }

        String className = clazz.getName();

        for (String nonCustomPackage : NON_CUSTOM_PACKAGES) {
            if (className.startsWith(nonCustomPackage)) {
                return false;
            }
        }

        for (String nonCustomPackage : ConfigUtil.getNonCustomPackageList()) {

            if (!nonCustomPackage.isEmpty() && className.startsWith(nonCustomPackage)) {
                return false;
            }
        }

        return true;
    }

    
    public static boolean isNeedField(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isPublic(modifiers)
                && !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers);
    }

    public static boolean isStaticClass(Class clazz) {
        return Modifier.isStatic(clazz.getModifiers());
    }
    
    public static String getTypeSimpleName(String typeName) {

        List<String> wordList = new ArrayList<String>();

        boolean lastCharValid = true;

        StringBuilder word = new StringBuilder();

        for (char ch : typeName.toCharArray()) {

            if (isValidJavaClassChar(ch) != lastCharValid) {

                wordList.add(word.toString());

                word.setLength(0);

                lastCharValid = !lastCharValid;
            }

            word.append(ch);
        }

        if (word.length() > 0) {
            wordList.add(word.toString());
        }

        StringBuilder finalStr = new StringBuilder();

        for (String str : wordList) {
            if (str.contains(".")) {

                finalStr.append(getClassSimpleName(str));
                continue;
            }

            finalStr.append(str);
        }
        return finalStr.toString();
    }

    public static String getClassSimpleName(String className) {

        String[] strArray = className.split("\\.");
        int length = strArray.length;

        return strArray[length - 1];
    }

    
    public static boolean isValidJavaClassChar(char ch) {
        return String.valueOf(ch).matches("[0-9a-zA-Z\\._$]");
    }

    
    public static boolean isValidJavaClassString(String str) {
        return str.matches("[0-9a-zA-Z\\._$]+");
    }

    public static String getInstanceName(String classSimpleName) {

        String instanceName = classSimpleName.substring(0, 1).toLowerCase();
        if (classSimpleName.length() > 1) {
            instanceName += classSimpleName.substring(1);
        }
        return instanceName;
    }

    public static String getNewStatement(Class clazz, String classSimpleName, String instanceName) {

        if (!isInnerClass(clazz)) {

            return getNewStatement2(classSimpleName, instanceName);
        }

        Class declaringClass = clazz.getDeclaringClass();

        if (declaringClass.getDeclaringClass() != null) {

            PrintUtil.infoPrint("不支持多于一层的内部类: " + clazz.getName());

            return getNewStatement2(classSimpleName, instanceName);
        }

        StringBuilder newStatement = new StringBuilder();

        String declaringClassSimpleName = declaringClass.getSimpleName();

        String innerClassInstanceName = getInstanceName(classSimpleName);

        if (isStaticClass(clazz)) {
            
            newStatement.append(declaringClassSimpleName)
                    .append(".")
                    .append(classSimpleName)
                    .append(" ")
                    .append(innerClassInstanceName)
                    .append(" = new ")
                    .append(declaringClassSimpleName)
                    .append(".")
                    .append(classSimpleName)
                    .append("();");
            return newStatement.toString();
        }
        
        String declaringClassInstanceName = getInstanceName(declaringClassSimpleName);
        newStatement.append(declaringClassSimpleName)
                .append(".")
                .append(classSimpleName)
                .append(" ")
                .append(innerClassInstanceName)
                .append(" = ")
                .append(declaringClassInstanceName)
                .append(".new ")
                .append(classSimpleName)
                .append("();");
        return newStatement.toString();
    }

    public static String getNewStatement2(String classSimpleName, String instanceName) {

        StringBuilder newStatement = new StringBuilder();
        newStatement.append(classSimpleName)
                .append(" ")
                .append(instanceName)
                .append(" = new ")
                .append(classSimpleName)
                .append("();");
        return newStatement.toString();
    }

    public static boolean isInnerClass(Class clazz) {
        
        return !clazz.isEnum() && clazz.getDeclaringClass() != null;
    }

    public static Class getClassFromName(String className) {

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            PrintUtil.debugPrint("未找到指定的类: " + e.getMessage());

            return null;
        }
    }

    private ClassUtil() {
        throw new IllegalStateException("illegal");
    }
}
