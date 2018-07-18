package com.ubtechinc.codemaosdk.event;

/**
 * @Deseription 机器人姿态事件
 * @Author tanghongyu
 * @Time 2018/4/10 20:01
 */

public class RobotPostureEvent {
    public int type;

    public RobotPostureEvent(int type) {
        this.type = type;
    }
}
