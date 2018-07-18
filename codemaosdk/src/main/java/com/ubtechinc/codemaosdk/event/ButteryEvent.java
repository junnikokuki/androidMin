package com.ubtechinc.codemaosdk.event;

/**
 * @Deseription 电池信息
 * @Author tanghongyu
 * @Time 2018/4/10 20:01
 */

public class ButteryEvent {
    public int level;
    public int status;

    public ButteryEvent(int level, int status) {
        this.level = level;
        this.status = status;
    }
}
