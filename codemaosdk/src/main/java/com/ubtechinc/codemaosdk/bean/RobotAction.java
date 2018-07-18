package com.ubtechinc.codemaosdk.bean;

/**
 * Created by tanghongyu on 2018/3/9.
 */

public class RobotAction {
    //动作ID
    private String id;
    //中文名
    private String cnName;
    //英文名
    private String enName;
    //动作描述
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
