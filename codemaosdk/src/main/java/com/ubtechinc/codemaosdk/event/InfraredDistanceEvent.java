package com.ubtechinc.codemaosdk.event;

/**
 * @Deseription 红外线距离事件
 * @Author tanghongyu
 * @Time 2018/4/10 20:01
 */

public class InfraredDistanceEvent {
    public int distance;

    public InfraredDistanceEvent(int distance) {
        this.distance = distance;
    }
}
