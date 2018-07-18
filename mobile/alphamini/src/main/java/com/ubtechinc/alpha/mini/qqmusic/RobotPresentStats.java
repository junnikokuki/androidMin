package com.ubtechinc.alpha.mini.qqmusic;

import android.util.Log;

/**
 * @desc : 机器人礼物领取状态
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/12
 */

public class RobotPresentStats {
    private static final String TAG = "RobotPresentStats";

    private String robotSn; //机器人流水号
    private boolean isReceive;

    private String robotEquipmentId;//机器人序列号

    public RobotPresentStats(String robotSn,String robotEquipmentId, boolean isReceive) {
        Log.i(TAG, " robotSn : " + robotSn);
        this.robotSn = robotSn;
        this.isReceive = isReceive;
        this.robotEquipmentId = robotEquipmentId;
    }

    public String getRobotSn() {
        return robotSn;
    }

    public void setRobotSn(String robotSn) {
        this.robotSn = robotSn;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public String getRobotEquipmentId() {
        return robotEquipmentId;
    }

    public void setRobotEquipmentId(String robotEquipmentId) {
        this.robotEquipmentId = robotEquipmentId;
    }

    @Override
    public String toString() {
        return "RobotPresentStats{" +
                "robotSn='" + robotSn + '\'' +
                ", isReceive=" + isReceive +
                ",robotEquipmentId = " +robotEquipmentId+
                '}';
    }
}
