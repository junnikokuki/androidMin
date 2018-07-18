package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.alpha.event.BaseEvent;

/**
 * @作者：liudongyang
 * @日期: 18/5/5 16:03
 * @描述: 从帐号接受权限回应事件
 */

public class RequestContronRightEvent{

    private boolean isAllow;

    public RequestContronRightEvent(boolean isAllow) {
        this.isAllow = isAllow;
    }

    public boolean isAllow() {
        return isAllow;
    }
}
