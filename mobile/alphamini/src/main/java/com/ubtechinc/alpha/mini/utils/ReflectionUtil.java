package com.ubtechinc.alpha.mini.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ubt on 2016/12/19.
 * 反射工具类
 */

public class ReflectionUtil {
    /**
     * 遍历当前类以及父类去查找方法，例子，写的比较简单
     * @param object
     * @param methodName
     * @param params
     * @param paramTypes
     * @return
     */
    public static Object invokeMethod(Object object, String methodName, Object[] params, Class[] paramTypes){
        Object returnObj = null;
        if (object == null) {
            return null;
        }
        Class cls = object.getClass();
        Method method = null;
        for (; cls != Object.class; cls = cls.getSuperclass()) { //因为取的是父类的默认修饰符的方法，所以需要循环找到该方法
            try {
                method = cls.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            } catch (SecurityException e) {
                //e.printStackTrace();
            }
        }
        if(method != null){
            method.setAccessible(true);
            try {
                returnObj = method.invoke(object, params);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return returnObj;
    }
}
