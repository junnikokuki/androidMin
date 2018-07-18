package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.nets.http.Url;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class CanclePermissionModule {

    @Url("alpha2-web/relation/unbind")
    public static class Request{
        private String slaveUserId;
        private String masterUserId;
        private String robotUserId;

        public Request(String robotUserId){
            this.robotUserId = robotUserId;
        }

        public Request(String slaveUserId, String robotUserId) {
            this(robotUserId);
            this.slaveUserId = slaveUserId;
        }

        public Request(String masterUserId,String robotUserId, String slaveUserId){
            this(slaveUserId, robotUserId);
            this.masterUserId = masterUserId;
        }
    }

    public static class Response {


        /**
         * success : true
         * msg : success
         * resultCode : 200
         * data : {}
         */

        private boolean success;
        private String msg;
        private int resultCode;
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

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
        }
    }
}
