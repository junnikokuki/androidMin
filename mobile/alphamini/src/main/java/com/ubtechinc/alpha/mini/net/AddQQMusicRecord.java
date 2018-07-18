package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.nets.http.Url;

/**
 * @author htoall
 * @Description: 上传QQ会员领取成功
 * @date 2018/4/21 下午9:18
 * @copyright TCL-MIE
 */
public class AddQQMusicRecord {
    @Url("alpha2-web/getservicerecord/add")
    public static class Request {
        private String robotUserId;
        private String serviceBeginTime;
        private String serviceEndTime;
        private String userType;
        private String nickName;
        private String serviceType;

        public Request(String robotUserId, String serviceBeginTime, String serviceEndTime, String userType, String nickName, String serviceType) {
            this.robotUserId = robotUserId;
            this.serviceBeginTime = serviceBeginTime;
            this.serviceEndTime = serviceEndTime;
            this.userType = userType;
            this.nickName = nickName;
            this.serviceType = serviceType;
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

        @Override
        public String toString() {
            return "Response{" +
                    "success=" + success +
                    ", msg='" + msg + '\'' +
                    ", resultCode=" + resultCode +
                    ", data=" + data +
                    '}';
        }
    }
}
