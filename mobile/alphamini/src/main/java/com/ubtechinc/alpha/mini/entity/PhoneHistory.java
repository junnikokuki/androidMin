package com.ubtechinc.alpha.mini.entity;

import com.ubtechinc.alpha.mini.R;

/**
 * 通话记录实体类.
 */

public class PhoneHistory {

    private String robotId;

    private String callId;

    private String callerName;

    private int type;

    private long duration;

    private long callTime;

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public String getDate() {
        return "";
    }

    public String getTime() {
        return "";
    }

    public String getSubTitle() {
        return "";
    }

    public int getSubTitleDrawable() {
        switch (type) {
            case 1:
                return R.drawable.ic_call;
            case 2:
                return R.drawable.ic_missed_calls;
            case 3:
                return R.drawable.ic_incoming;
        }
        return R.drawable.ic_call;
    }
}
