package com.ubtechinc.alpha.mini.avatar.entity;


import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/6/9 13:49
 * @modifier：ubt
 * @modify_date：2017/6/9 13:49
 * [A brief description]
 * version
 */

public class AvatarResponse {
    private boolean status;
    private String info;
    private List<AvatarActionModel> models;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<AvatarActionModel> getModels() {
        return models;
    }

    public void setModels(List<AvatarActionModel> models) {
        this.models = models;
    }
}
