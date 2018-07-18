package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.nets.http.Url;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class UpdatePermissionModule {

    @Url("alpha2-web/permission/update")
    public static class Request{
        private String slaveUserId;
        private String permissions;
        private String robotUserId;

        public Request(String slaveUserId, String robotUserId, String permissions) {
            this.slaveUserId = slaveUserId;
            this.permissions = permissions;
            this.robotUserId = robotUserId;
        }
    }

    public static class Response {

        /**
         * success : true
         * returnCode : 200
         * msg : “success”
         */

        private boolean success;
        private int returnCode;
        private String msg;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(int returnCode) {
            this.returnCode = returnCode;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
