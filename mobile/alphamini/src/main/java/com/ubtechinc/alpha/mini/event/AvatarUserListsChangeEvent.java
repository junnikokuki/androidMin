package com.ubtechinc.alpha.mini.event;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/22 18:32
 * @描述: 用户列表发生变化
 */

public class AvatarUserListsChangeEvent {

    private List<String> allUsers;

    private List<String> enterUserIds;

    private List<String> leaveUserIds;

    public AvatarUserListsChangeEvent(List<String> allUsers, List<String> enterUserIds, List<String> leaveUserIds) {
        this.allUsers = allUsers;
        this.enterUserIds = enterUserIds;
        this.leaveUserIds = leaveUserIds;
    }


    public List<String> getAllUsers() {
        return allUsers;
    }

    public List<String> getEnterUserIds() {
        return enterUserIds;
    }

    public List<String> getLeaveUserIds() {
        return leaveUserIds;
    }
}
