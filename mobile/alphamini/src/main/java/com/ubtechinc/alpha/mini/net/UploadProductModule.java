package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * Created by junsheng.chen on 2018/7/10.
 */
@Keep
public class UploadProductModule {

    public static final int FILE = 3;

    @Url("creation/opus/upload")
    @Keep
    public class Request {

        String coverUrl;

        String description;

        List<Files> files;

        String name;

        String title;

        int type;

        String userId;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public List<Files> getFiles() {
            return files;
        }

        public void setFiles(List<Files> files) {
            this.files = files;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    @Keep
    public class Response {

        String status;

        public boolean getStatus() {
            return Boolean.valueOf(status);
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Keep
    public static class Files {

        String description;

        String fileName;

        String fileUrl;

        int order;

        String thumbnail;

        int type;

        public String getDescription() {
            return description;
        }

        public int getType() {
            return type;
        }

        public int getOrder() {
            return order;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }

}
