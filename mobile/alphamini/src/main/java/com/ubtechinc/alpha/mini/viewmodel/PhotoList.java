package com.ubtechinc.alpha.mini.viewmodel;

/**
 * Created by hongjie.xiang on 2017/10/30.
 */

import android.util.Log;


import java.io.Serializable;
import java.util.List;

public class PhotoList implements Serializable,Comparable<PhotoList>{

    /**
     *
     */
    private static final long serialVersionUID = -2870791792134072493L;
    private String dateType;
    private List<ImageModel> imageDetailList  ;

    public String getDateType() {
        return dateType;
    }
    public void setDateType(String dateType) {
        this.dateType = dateType;
    }
    public List<ImageModel> getImageDetailList() {
        return imageDetailList;
    }
    public void setImageDetailList(List<ImageModel> imageDetailList) {
        this.imageDetailList = imageDetailList;
    }
    @Override
    public int compareTo(PhotoList another) {
        int thisDate = Integer.valueOf(this.dateType.replace("-", ""));
        int compDate = Integer.valueOf(another.dateType.replace("-", ""));
        if( compDate>thisDate){
            Log.i("BookComment", "thisdate="+thisDate+";comparedate ="+compDate);
            return 1;
        }
        return -1;
    }

}