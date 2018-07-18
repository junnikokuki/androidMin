package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.alpha.event.BaseEvent;

/**
 * Created by ubt on 2018/1/11.
 */

public class RobotImResponseEvent extends BaseEvent {

    public String robotId;

    public RobotImResponseEvent(String robotId){
        this.robotId = robotId;
    }

}
