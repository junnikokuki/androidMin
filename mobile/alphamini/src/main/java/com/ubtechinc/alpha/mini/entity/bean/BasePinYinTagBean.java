package com.ubtechinc.alpha.mini.entity.bean;

import java.io.Serializable;

/**
 * 介绍：索引类的标志位的实体基类
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * CSDN：http://blog.csdn.net/zxt0601
 * 时间： 16/09/04.
 */

public abstract class BasePinYinTagBean implements IIndexTargetInterface, Serializable {
    private String firstLetterPy;//第一个字母的拼音

    public String getFirstLetterPy() {
        return firstLetterPy;
    }

    public void setFirstLetterPy(String firstLetterPy) {
        this.firstLetterPy = firstLetterPy;
    }
}
