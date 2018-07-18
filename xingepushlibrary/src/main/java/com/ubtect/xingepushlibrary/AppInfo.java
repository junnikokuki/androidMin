package com.ubtect.xingepushlibrary;

/**
 * @Date: 2017/10/31.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class AppInfo {
    private String accessId;
    private String accessKey;
    private String appId;
    private String appName;
    private String createTime;
    private String device;
    private String id;
    private String remark;
    private String secretKey;
    private String type;
    private String updateTime;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "accessId='" + accessId + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", device='" + device + '\'' +
                ", id='" + id + '\'' +
                ", remark='" + remark + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", type='" + type + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
