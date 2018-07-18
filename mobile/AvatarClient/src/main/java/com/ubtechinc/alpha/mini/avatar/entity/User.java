package com.ubtechinc.alpha.mini.avatar.entity;

/**
 * @作者：liudongyang
 * @日期: 18/5/24 18:25
 * @描述:
 */

public class User {

    private String avtar;

    private String name;

    private String id;

    private boolean isEnterAlreadyShow;

    private boolean isExitAlreadyShow;

    public boolean isExitAlreadyShow() {
        return isExitAlreadyShow;
    }

    public void setExitAlreadyShow(boolean exitAlreadyShow) {
        isExitAlreadyShow = exitAlreadyShow;
    }

    public boolean isEnterAlreadyShow() {
        return isEnterAlreadyShow;
    }

    public void setEnterAlreadyShow(boolean enterAlreadyShow) {
        isEnterAlreadyShow = enterAlreadyShow;
    }

    public String getAvtar() {
        return avtar;
    }

    public void setAvtar(String avtar) {
        this.avtar = avtar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

