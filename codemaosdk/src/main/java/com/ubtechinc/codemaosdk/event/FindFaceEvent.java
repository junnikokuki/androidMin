package com.ubtechinc.codemaosdk.event;

/**
 * @Deseription 电池信息
 * @Author tanghongyu
 * @Time 2018/4/10 20:01
 */

public class FindFaceEvent {
    public int status;
    public int faceCount;
    public int code;
    public FindFaceEvent(int type, int faceCount, int code) {
        this.status = type;
        this.faceCount = faceCount;
        this.code = code;
    }
}
