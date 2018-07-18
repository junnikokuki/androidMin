package com.ubtechinc.alpha.mini.widget.viewstack;

/**
 * Created by junsheng.chen on 2018/7/6.
 */
public interface Stack<T> {

    /*判空*/
    boolean isEmpty();
    /*清空栈*/
    void clear();
    /*弹栈*/
    T pop();
    /*入栈*/
    boolean push(T data);
    /*栈的长度*/
    int length();
    /*查看栈顶的元素，但不移除它*/
    T peek();
    /*返回对象在栈中的位置*/
    int search(T t);
}
