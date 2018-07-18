package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/14
 * 获取绑定信息的接口
 */

@Keep
public class GetMobileInfoModule {

    @Url("user-service-rest/v2/user/account/extra")
    @Keep
    public static class Request {
        private int appId;

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }
    }

    @Keep
    public class Response {

        /**
         * appId : 0
         * expandEmail : string
         * expandPhone : string
         * id : 0
         * remark : string
         * userId : 0
         */

        private int appId;
        private String expandEmail;
        private String expandPhone;
        private int id;
        private String remark;
        private int userId;

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getExpandEmail() {
            return expandEmail;
        }

        public void setExpandEmail(String expandEmail) {
            this.expandEmail = expandEmail;
        }

        public String getExpandPhone() {
            return expandPhone;
        }

        public void setExpandPhone(String expandPhone) {
            this.expandPhone = expandPhone;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

}
