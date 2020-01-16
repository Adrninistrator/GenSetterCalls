package com.github.adrninistrator.gensettercalls.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

public class ClipboardUtil {

    public static Class getClassInClipBoard() {

        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable content = clipboard.getContents(null);
            if (content == null) {
                PrintUtil.infoPrint("剪切板内容为空");

                return null;
            }

            if (content.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

                return getClassInClipBoard4File(content.getTransferData(DataFlavor.javaFileListFlavor));
            }

            if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {

                return getClassInClipBoard4String(content.getTransferData(DataFlavor.stringFlavor));
            }

            PrintUtil.infoPrint("剪切板内容的类型未知");

            return null;
        } catch (Exception e) {
            e.printStackTrace();

            PrintUtil.infoPrint("从剪切板获取需要生成set方法调用代码的类失败");

            return null;
        }

    }

    private static Class getClassInClipBoard4File(Object o) {

        PrintUtil.debugPrint("剪切板内容为文件列表");

        if (o == null) {

            PrintUtil.infoPrint("剪切板文件内容为空");
            return null;
        }

        List<File> fileList = (List<File>) o;
        if (fileList == null || fileList.isEmpty()) {
            PrintUtil.infoPrint("从剪切板获取类型为文件的内容不存在");
            return null;
        }

        String filePath = fileList.get(0).getAbsolutePath();

        if (!FileUtil.isJavaOrClassFile(filePath)) {
            PrintUtil.infoPrint("从剪切板获取到文件类型不是Java文件: " + filePath);
            return null;
        }

        return getClassFromJavaFilePath(filePath);
    }

    private static Class getClassInClipBoard4String(Object o) {

        PrintUtil.debugPrint("剪切板内容为字符串");

        if (o == null) {
            PrintUtil.infoPrint("剪切板字符串内容为空");
            return null;
        }

        String str = (String) o;

        if (FileUtil.isJavaOrClassFile(str)) {

            return getClassFromJavaFilePath(str);
        }

        if (ClassUtil.isValidJavaClassString(str) && str.contains(".")) {
            PrintUtil.infoPrint("获取到剪切板中的Java类名为: " + str);

            Class clazz = ClassUtil.getClassFromName(str);
            if (clazz != null) {
                PrintUtil.infoPrint("获取到剪切板对应的类为: " + str);
                return clazz;
            }

            PrintUtil.infoPrint("未获取到剪切板对应的类");
            return null;
        }

        PrintUtil.infoPrint("获取到剪切板中的字符串内容非法: " + str);

        return null;
    }
    
    private static Class getClassFromJavaFilePath(String filePath) {

        PrintUtil.infoPrint("获取到剪切板中的Java文件为: " + filePath);

        int extIndex = filePath.lastIndexOf('.');

        String filePathNew = filePath.substring(0, extIndex);

        String[] filePathArray = FileUtil.splitFilePath(filePathNew);

        int arrayLength = filePathArray.length;

        
        int lastIllegalIndex = -1;

        for (int i = 0; i < arrayLength; i++) {
            if (!ClassUtil.isValidJavaClassString(filePathArray[i])) {
                lastIllegalIndex = i;
            }
        }

        for (int i = lastIllegalIndex + 1; i < arrayLength; i++) {

            String className = getClassNameFromArray(filePathArray, i);

            Class clazz = ClassUtil.getClassFromName(className);
            if (clazz != null) {
                PrintUtil.infoPrint("获取到剪切板对应的Java文件对应的类为: " + className);
                return clazz;
            }
        }

        PrintUtil.infoPrint("未获取到剪切板对应的Java文件对应的类");
        return null;
    }

    private static String getClassNameFromArray(String[] array, int index) {

        int arrayLength = array.length;

        StringBuilder className = new StringBuilder();

        for (int i = index; i < arrayLength; i++) {

            if (className.length() > 0) {
                className.append(".");
            }
            className.append(array[i]);
        }
        return className.toString();
    }

    public static void setStringToClipboard(String data) {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new StringSelection(data), null);
    }

    private ClipboardUtil() {
        throw new IllegalStateException("illegal");
    }
}
