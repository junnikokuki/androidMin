package com.ubtechinc.alpha.mini.entity;

import java.util.List;

/**
 * Created by ubt on 2018/2/3.
 */

public class Page<T> {

    private long versionCode;

    private int currentPage; //IM 指令用currentPage

    private int from; // 本地数据库 用 from

    private int totalPage = -1;

    private List<T> data;

    private String robotId;

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}
