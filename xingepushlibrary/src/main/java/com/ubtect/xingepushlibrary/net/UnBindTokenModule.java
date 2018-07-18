package com.ubtect.xingepushlibrary.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @Date: 2017/11/17.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UnBindTokenModule {
    @Url("xinge-push-rest/push/unbindToken")
    @Keep
    public class Request {
        private int appId;
        private String createTime;
        private String token;
        private int userId;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    @Keep
    public class Response {
        private boolean unbindStatus;

        public boolean getUnbindSuccess() {
            return unbindStatus;
        }

        public void setUnbindStatus(boolean unbindStatus) {
            this.unbindStatus = unbindStatus;
        }
    }
}
