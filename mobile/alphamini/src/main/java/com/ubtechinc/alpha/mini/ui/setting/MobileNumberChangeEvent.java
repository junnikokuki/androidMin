package com.ubtechinc.alpha.mini.ui.setting;

/**
 * @作者：liudongyang
 * @日期: 18/4/20 17:55
 * @描述: 绑定号码变更事件
 */

public class MobileNumberChangeEvent {

    private String newPhoneNumber;

    public MobileNumberChangeEvent(String newPhoneNumber) {
        this.newPhoneNumber = newPhoneNumber;
    }

    public String getNewPhoneNumber() {
        return newPhoneNumber;
    }
}
