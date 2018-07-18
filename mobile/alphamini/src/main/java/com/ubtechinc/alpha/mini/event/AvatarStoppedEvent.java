package com.ubtechinc.alpha.mini.event;

import android.text.TextUtils;

public class AvatarStoppedEvent {

    private int reasonCode;

    private String reason;

    public AvatarStoppedEvent(String reason, int reasonCode) {
        this.reason = reason;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public String getReason() {
        if(TextUtils.isEmpty(reason)){
            reason = "悟空视频因未知原因退出";
        }
        return reason;
    }
}
