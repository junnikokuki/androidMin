package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.alpha.mini.entity.RobotPermission;

/**
 * 权限发生改变时的消息
 * <p>
 * Created by ubt on 2018/1/4.
 */

public class PermissionChangeEvent {

    public RobotPermission permission;
    public String robotId;

    public PermissionChangeEvent(String robotId,RobotPermission permission){
        this.permission = permission;
        this.robotId = robotId;
    }
}
