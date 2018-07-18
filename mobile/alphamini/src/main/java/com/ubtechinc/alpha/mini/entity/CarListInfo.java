package com.ubtechinc.alpha.mini.entity;

/**
 * Created by riley.zhang on 2018/7/6.
 */

public class CarListInfo {

    String mDeviceBleName;
    String mDeviceMac;

    public String getDeviceMac() {
        return mDeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.mDeviceMac = deviceMac;
    }

    public void setDeviceBleName(String deviceName) {
        this.mDeviceBleName = deviceName;
    }

    public String getDeviceBleName() {
        return mDeviceBleName;
    }
}
