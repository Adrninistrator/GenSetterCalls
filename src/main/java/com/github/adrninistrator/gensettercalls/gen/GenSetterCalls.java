package com.github.adrninistrator.gensettercalls.gen;

import com.github.adrninistrator.gensettercalls.comparator.FieldNameComparator;
import com.github.adrninistrator.gensettercalls.comparator.StringComparator;
import com.github.adrninistrator.gensettercalls.utils.ClassUtil;
import com.github.adrninistrator.gensettercalls.utils.ConfigUtil;
import com.github.adrninistrator.gensettercalls.utils.PrintUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class GenSetterCalls {

    public void handleClasses(String[] classNames) {

        for (String className : classNames) {
            Class clazz = ClassUtil.getClassFromName(className);
            if (clazz == null) {
                PrintUtil.infoPrint("找不到指定的类: " + className);
                continue;
            }

            handleClass(clazz);
        }
    }
    
    public void handleClasses(Class[] classArray) {

        for (Class clazz : classArray) {
            handleClass(clazz);
        }
    }

    public String handleClass(Class clazz) {

        Map<Class, List<Field>> allClassesFields = new HashMap<Class, List<Field>>();

        List<Class> allClassList = new ArrayList<Class>();

        Queue<Class> unhandledClassesQueue = new LinkedBlockingQueue<Class>();

        unhandledClassesQueue.add(clazz);

        while (!unhandledClassesQueue.isEmpty()) {

            Class unhandledClass = unhandledClassesQueue.poll();

            if (allClassList.contains(unhandledClass)) {
                PrintUtil.debugPrint("已获取过该类的字段，不再获取: " + unhandledClass.getName());
                continue;
            }

            allClassList.add(unhandledClass);
            PrintUtil.debugPrint("开始获取该类的全部字段: " + unhandledClass.getName());

            getAllClassesFields(unhandledClass, allClassesFields, unhandledClassesQueue);
        }

        PrintUtil.debugPrint("");

        PrintUtil.infoPrint("///////////////////////// " + clazz.getName());

        StringBuilder setterCallsResult = new StringBuilder();

        if (!ConfigUtil.isHideImport()) {

            List<String> classNameList = new ArrayList<String>(allClassList.size());

            for (Class currentClazz : allClassList) {
                if (!ClassUtil.isInnerClass(currentClazz)) {

                    classNameList.add(currentClazz.getName());
                }
            }

            Collections.sort(classNameList, StringComparator.getInstance());

            for (String className : classNameList) {
                setterCallsResult.append("import ").append(className).append(";").append("\r\n");
            }

            setterCallsResult.append("\r\n");
        }

        for (Class printClass : allClassList) {

            List<Field> fieldList = allClassesFields.get(printClass);

            if (fieldList == null) {
                throw new RuntimeException("获取对应的变量不存在: " + printClass.getName());
            }

            genResultString(printClass, fieldList, setterCallsResult);
        }

        String setterCallsResultStr = setterCallsResult.toString();

        PrintUtil.infoPrint(setterCallsResultStr);

        return setterCallsResultStr;
    }

    private void getAllClassesFields(Class clazz, Map<Class, List<Field>> allClassesFields, Queue<Class> unhandledClassesQueue) {

        List<Field> fieldList = new ArrayList<Field>();

        getAllFields(clazz, fieldList);

        addUnhandledClasses(fieldList, unhandledClassesQueue);

        allClassesFields.put(clazz, fieldList);
    }

    private void getAllFields(Class clazz, List<Field> fieldList) {

        List<Class> classList = new ArrayList<Class>();

        classList.add(clazz);

        Class parentClass = clazz.getSuperclass();
        while (true) {
            if (parentClass == null || parentClass == Object.class) {
                break;
            }

            classList.add(parentClass);
            PrintUtil.debugPrint(clazz.getName() + " 的超类: " + parentClass.getName());

            parentClass = parentClass.getSuperclass();
        }

        int size = classList.size();
        for (int i = size - 1; i >= 0; i--) {

            getFields(classList.get(i), fieldList);
        }
    }

    private void getFields(Class clazz, List<Field> fieldList) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            if (ClassUtil.isNeedField(field) && !fieldList.contains(field)) {
                fieldList.add(field);
            }
        }
    }

    private void addUnhandledClasses(List<Field> fieldList, Queue<Class> unhandledClassesQueue) {

        for (Field field : fieldList) {

            handleFieldClasses(field, unhandledClassesQueue);
        }
    }

    private void handleFieldClasses(Field field, Queue<Class> unhandledClassesQueue) {

        Class clazz = field.getType();

        if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {

            getCollectionFieldClasses(field, unhandledClassesQueue);

            return;
        }

        if (clazz.isArray()) {

            getArrayClasses(field, unhandledClassesQueue);

            return;
        }

        if (ClassUtil.isCustomType(clazz)) {
            PrintUtil.debugPrint("添加需要处理的自定义类型: " + field.getName() + " " + clazz.getName());
            unhandledClassesQueue.add(clazz);
        }
    }

    private void getCollectionFieldClasses(Field field, Queue<Class> unhandledClassesQueue) {

        List<Class> collectionClasses = new ArrayList<Class>();

        getCollectionTypeClasses(field.getGenericType(), collectionClasses);

        for (Class collectionClass : collectionClasses) {

            if (ClassUtil.isCustomType(collectionClass)) {
                PrintUtil.debugPrint("添加需要处理的Collection或Map对应的自定义类型: " + field.getName() + " " + collectionClass.getName());
                unhandledClassesQueue.add(collectionClass);
            }
        }
    }

    private void getArrayClasses(Field field, Queue<Class> unhandledClassesQueue) {

        Class classArray = field.getType().getComponentType();

        while (classArray.isArray()) {

            classArray = classArray.getComponentType();

            if (ClassUtil.isCustomType(classArray)) {
                PrintUtil.debugPrint("添加需要处理的数组对应的自定义类型: " + field.getName() + " " + classArray.getName());
                unhandledClassesQueue.add(classArray);
                break;
            }
        }
    }

    private void getCollectionTypeClasses(Type type, List<Class> collectionClasses) {

        if (!(type instanceof ParameterizedType)) {
            throw new RuntimeException("集合的Type不是ParameterizedType实例: " + type.getClass().getName());
        }

        ParameterizedType parameterizedType = (ParameterizedType) type;

        Type[] actualTypes = parameterizedType.getActualTypeArguments();

        for (Type actualType : actualTypes) {

            if (actualType instanceof ParameterizedType) {

                getCollectionTypeClasses(actualType, collectionClasses);
            } else {

                if (!(actualType instanceof Class)) {

                    PrintUtil.debugPrint(type.getClass().getName() + " 获取到ActualTypeArguments中的当前Type不是Class: " + actualType.getClass().getName());
                    return;
                }

                Class actureClass = (Class) actualType;
                PrintUtil.debugPrint(type.getClass().getName() + " 获取到Collection或Map对应的类型: " + actureClass.getName());

                collectionClasses.add(actureClass);
            }
        }
    }

    private String genResultString(Class clazz, List<Field> fieldList, StringBuilder setterCallsResult) {

        String classSimpleName = clazz.getSimpleName();

        if (ConfigUtil.isSortFieldByName()) {

            Collections.sort(fieldList, FieldNameComparator.getInstance());
        }

        String instanceName = ClassUtil.getInstanceName(classSimpleName);

        setterCallsResult.append(ClassUtil.getNewStatement(clazz, classSimpleName, instanceName)).append("\r\n");

        for (Field field : fieldList) {

            String fieldName = field.getName();

            String setterNameShow = fieldName.substring(0, 1).toUpperCase();
            if (fieldName.length() > 1) {
                setterNameShow += fieldName.substring(1);
            }

            String type = "";

            if (!ConfigUtil.isHideType()) {
                
                if (field.getGenericType() instanceof ParameterizedType) {
                    
                    type = ClassUtil.getTypeSimpleName(field.getGenericType().toString());
                } else {
                    
                    type = field.getType().getSimpleName();
                }
            }

            setterCallsResult.append(instanceName)
                    .append(".set")
                    .append(setterNameShow)
                    .append("(")
                    .append(type)
                    .append(");\r\n");
        }

        setterCallsResult.append("\r\n");

        return setterCallsResult.toString();
    }
}
