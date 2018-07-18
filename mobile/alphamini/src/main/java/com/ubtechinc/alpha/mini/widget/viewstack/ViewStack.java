package com.ubtechinc.alpha.mini.widget.viewstack;

import com.ubtechinc.alpha.mini.widget.viewpage.page.Page;

/**
 * Created by junsheng.chen on 2018/7/6.
 */
public class ViewStack implements Stack<Page> {

    private Page[] t = new Page[16];
    private int size = 0;

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < t.length; i++) {
            t[i] = null;//将其引用只为null，方便gc进行回收
        }
        size = 0;
    }

    @Override
    public Page pop() {
        if (size == 0)
            return null;
        else {
            Page tmp = t[size - 1];
            t[size - 1] = null;//便于gc回收
            size--;
            return tmp;
        }
    }

    @Override
    public boolean push(Page data) {
        if (size >= t.length) {
            //栈空间已满，需要扩容
            resize();
        }
        t[size++] = data;
        return true;
    }

    private void resize() {
        Page[] tmp = (Page[]) new Object[t.length * 2];
        for (int i = 0; i < t.length; i++) {
            tmp[i] = t[i];
            t[i] = null;//便于gc处理
        }
        t = tmp;
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public Page peek() {
        if (size == 0) {
            return null;
        } else {
            return t[size - 1];
        }
    }

    @Override
    public int search(Page view) {
        for (int i = 0; i < size; i++) {
            if (t.equals(this.t[i])) {
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ViewStack:\n[\n");
        for (int i = size - 1; i >= 0; i--) {
            sb.append("   " + t[i].toString());
            if (i != size + 1) {
                sb.append("\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
