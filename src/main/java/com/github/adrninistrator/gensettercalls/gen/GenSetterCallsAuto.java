package com.github.adrninistrator.gensettercalls.gen;

import com.github.adrninistrator.gensettercalls.utils.ClipboardUtil;
import com.github.adrninistrator.gensettercalls.utils.PrintUtil;

public class GenSetterCallsAuto {

    public static void main(String[] args) {

        Class clazz = ClipboardUtil.getClassInClipBoard();
        if (clazz == null) {
            return;
        }

        String setterCallsResult = new GenSetterCalls().handleClass(clazz);
        if (setterCallsResult != null && !setterCallsResult.isEmpty()) {

            ClipboardUtil.setStringToClipboard(setterCallsResult);

            PrintUtil.infoPrint("以上代码已复制到剪切板");

            return;
        }

        PrintUtil.infoPrint("生成结果为空");
    }
}
