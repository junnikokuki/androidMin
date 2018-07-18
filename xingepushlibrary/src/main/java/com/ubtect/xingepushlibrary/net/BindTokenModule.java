package com.ubtect.xingepushlibrary.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.util.List;
import java.util.Objects;

/**
 * @Date: 2017/11/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */
@Keep
public class BindTokenModule {
    @Url("xinge-push-rest/push/userToken")
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
        private boolean bindStatus;

        public boolean getBindSuccess() {
            return bindStatus;
        }

        public void setBindStatus(boolean bindStatus) {
            this.bindStatus = bindStatus;
        }
    }
}
