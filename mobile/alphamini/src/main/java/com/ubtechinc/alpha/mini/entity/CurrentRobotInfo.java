package com.ubtechinc.alpha.mini.entity;

public class CurrentRobotInfo extends RobotInfo{

    private RobotInfo robotInfo;

    @Override
    public int getOnlineState() {
        return robotInfo.getOnlineState();
    }

    @Override
    public String getRobotName() {
        return robotInfo.getRobotName();
    }

    @Override
    public String getMasterUserId() {
        return robotInfo.getMasterUserId();
    }

    @Override
    public String getRobotUserId() {
        return robotInfo.getRobotUserId();
    }

    @Override
    public String getMasterUserName() {
        return robotInfo.getMasterUserName();
    }

    public void setRobotInfo(RobotInfo robotInfo) {
        this.robotInfo = robotInfo;
    }
}
