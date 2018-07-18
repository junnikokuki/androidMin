package com.ubtechinc.alpha.mini.entity;

/**
 * Created by ubt on 2018/2/5.
 */

public class SimState {

    private boolean hasSim;

    private String phoneNum;

    private boolean cellData;

    private boolean roamData;

    public SimState(boolean hasSim, String phoneNum) {
        this.hasSim = hasSim;
        this.phoneNum = phoneNum;
    }

    public SimState(){

    }

    public boolean isHasSim() {
        return hasSim;
    }

    public void setHasSim(boolean hasSim) {
        this.hasSim = hasSim;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isCellData() {
        return cellData;
    }

    public void setCellData(boolean cellData) {
        this.cellData = cellData;
    }

    public boolean isRoamData() {
        return roamData;
    }

    public void setRoamData(boolean roamData) {
        this.roamData = roamData;
    }
}
