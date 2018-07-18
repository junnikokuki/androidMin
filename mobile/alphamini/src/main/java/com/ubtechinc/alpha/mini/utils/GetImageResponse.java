package com.ubtechinc.alpha.mini.utils;

/**
 * Created by Administrator on 2017/10/30.
 */

import com.ubtechinc.alpha.mini.viewmodel.ImageModel;

import java.util.List;

public class GetImageResponse {
    private boolean status;
    private String info;
    private List<ImageModel> models;
    private int count;
    private int totalPage;
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public List<ImageModel> getModels() {
        return models;
    }
    public void setModels(List<ImageModel> models) {
        this.models = models;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getTotalPage() {
        return totalPage;
    }
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

}