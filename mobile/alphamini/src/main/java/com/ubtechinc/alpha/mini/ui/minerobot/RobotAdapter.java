package com.ubtechinc.alpha.mini.ui.minerobot;

import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

/**
 * @desc : 机器人信息接口适配器
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/13
 */

public class RobotAdapter implements IRobotInfo{
    private RobotInfo robotInfo;

    public RobotAdapter(RobotInfo robotInfo) {
        this.robotInfo = robotInfo;
    }

    @Override
    public String getUserId() {
        return robotInfo.getRobotUserId();
    }

    @Override
    public String getShareUser() {
        // TODO 增加用户信息
        return "";
    }

    @Override
    public boolean isMaster() {
        return robotInfo.getMasterUserId() == AuthLive.getInstance().getUserId();
    }
}
