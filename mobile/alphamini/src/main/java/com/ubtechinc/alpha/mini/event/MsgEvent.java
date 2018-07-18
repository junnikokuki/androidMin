package com.ubtechinc.alpha.mini.event;

/**
 * Created by junsheng.chen on 2018/7/4.
 */
public final class MsgEvent {

    public int msgCount;

    public boolean hasNew;

    public MsgEvent(int msgCount, boolean hasNew) {
        this.msgCount = msgCount;
        this.hasNew = hasNew;
    }
}
