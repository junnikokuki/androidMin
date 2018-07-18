package com.ubtechinc.alpha.mini.ui.bind;

import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.io.Serializable;
import java.util.List;

/**
 * @Date: 2018/2/28.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UserList implements Serializable {

    private List<CheckBindRobotModule.User> users;

    public UserList(List<CheckBindRobotModule.User> users) {
        this.users = users;
    }

    public List<CheckBindRobotModule.User> getUsers() {
        return users;
    }

}