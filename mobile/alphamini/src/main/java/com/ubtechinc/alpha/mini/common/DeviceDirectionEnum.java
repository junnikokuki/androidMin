package com.ubtechinc.alpha.mini.common;

/**
 * @ClassName DeviceDirectionEnum
 *
 * @author wmma
 * @date 2017/3/6
 * @Description: 枚举手机/平板状态
 *
 */

public enum DeviceDirectionEnum {

    NONE("none", 0),       //None
    LEFT("left", 1),       //向左倾斜
    RIGHT("right", 2),     //向右倾斜
    UP("up", 3),           //向上倾斜
    DOWN("down", 4),       //向下倾斜
    SWING("swing", 5);     //左右摇晃


    DeviceDirectionEnum(String value, int type) {
        this.value = value;
        this.type = type;
    }

    private String value;
    private int type;

    public String getValue() {
        return this.value;
    }

    public int getType() {
        return type;
    }
}

