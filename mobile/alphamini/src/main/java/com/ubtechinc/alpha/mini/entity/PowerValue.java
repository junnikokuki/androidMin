package com.ubtechinc.alpha.mini.entity;

/**
 * Created by ubt on 2018/3/17.
 */

public class PowerValue {

    public boolean isCharging;

    public boolean isLowPower;

    public int value;

    public String getValueStr(){
        if(value == 0){
            return "未知";
        }
        return value +"%";
    }

    public int getLevel(){
        if(isLowPower){
            return 0;
        }
        if(value == 0){
            return 1000;
        }
        return value/10;
    }
}
