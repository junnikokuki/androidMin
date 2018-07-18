package com.ubtechinc.alpha.mini.viewmodel;

/**
 * Created by Administrator on 2017/10/30.
 */


import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageModel implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -9094409249516044759L;
    private String path;
    private long UploadTime;
    private String Thumbnail;
    private String Name;
    private String image_original_url;
    private String image_thumbnail_url;
    private String  image_upload_time;
    private String image_id;
    private int image_type;
    private long createTime;

    public int getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(int thumbSize) {
        this.thumbSize = thumbSize;
    }

    private int thumbSize ;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getUploadTime() {
        return UploadTime;
    }

    public void setUploadTime(long uploadTime) {
        UploadTime = uploadTime;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getImage_original_url() {
        return image_original_url;
    }
    public void setImage_original_url(String image_original_url) {
        this.image_original_url = image_original_url;
    }
    public String getImage_thumbnail_url() {
        return image_thumbnail_url;
    }
    public void setImage_thumbnail_url(String image_thumbnail_url) {
        this.image_thumbnail_url = image_thumbnail_url;
    }
    public String getImage_upload_time() {
        return image_upload_time;
    }
    public void setImage_upload_time(String image_upload_time) {
        this.image_upload_time = image_upload_time;
    }
    public String getImage_id() {
        return image_id;
    }
    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public int getImage_type() {
        return image_type;
    }


    public String getDate() {
        Log.d("xxxxxx","new Date="+String.valueOf(getCreateTime()));
        return new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(getCreateTime()));
    }

    public String getDate1(){
        return new SimpleDateFormat("yyyy年MM月dd日")
                .format(new Date(getCreateTime()));
    }
}
