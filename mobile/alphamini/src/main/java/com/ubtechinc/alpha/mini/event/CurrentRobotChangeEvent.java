package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.alpha.mini.entity.RobotInfo;

/**
 * 当前机器人改变是的事件
 *
 * Created by ubt on 2018/1/4.
 */

public class CurrentRobotChangeEvent {

    public RobotInfo currentRobotInfo;

    public CurrentRobotChangeEvent(RobotInfo currentRobotInfo){
        this.currentRobotInfo = currentRobotInfo;
    }
}
