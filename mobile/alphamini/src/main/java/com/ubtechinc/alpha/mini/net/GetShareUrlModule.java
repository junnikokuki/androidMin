package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;
import com.ubtechinc.nets.http.Url;

/**
 * @desc : 获取分享Url链接
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/2
 */

public class GetShareUrlModule {

    @Url("alpha2-web/sharelink/getShareId")
    @Keep
    public static class Request{
        private String robotUserId;
        private String toUserId;
        private String permissions;
        private String expireHours;
        private String shareUrlType;

        public Request(String robotUserId) {
            this.robotUserId = robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }

        public void setExpireHours(String expireHours) {
            this.expireHours = expireHours;
        }

        public void setShareUrlType(String shareUrlType) {
            this.shareUrlType = shareUrlType;
        }
    }

    @Keep
    public static class Response{

        /**
         * success : true
         * msg : "success"
         * data : {"result":{"inviteCode":"xxxxxxxxxxxx","url":"http://xxxxxxxxxxxx"}}
         */

        private boolean success;
        private String msg;
        private DataBean data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }
    }

    @Keep
    public static class DataBean {
        /**
         * result : {"inviteCode":"xxxxxxxxxxxx","url":"http://xxxxxxxxxxxx"}
         */

        private ResultBean result;

        public ResultBean getResult() {
            return result;
        }

        public void setResult(ResultBean result) {
            this.result = result;
        }
    }

    @Keep
    public static class ResultBean {
        /**
         * inviteCode : xxxxxxxxxxxx
         * url : http://xxxxxxxxxxxx
         */

        private String inviteCode;
        private String url;

        public String getInviteCode() {
            return inviteCode;
        }

        public void setInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
