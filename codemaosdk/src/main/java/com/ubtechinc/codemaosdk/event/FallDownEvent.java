package com.ubtechinc.codemaosdk.event;

/**
 * @Deseription 摔倒爬起事件
 * @Author tanghongyu
 * @Time 2018/4/10 20:01
 */

public class FallDownEvent {
    public int type;

    public FallDownEvent(int type) {
        this.type = type;
    }
}
