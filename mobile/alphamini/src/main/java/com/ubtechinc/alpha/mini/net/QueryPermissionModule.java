package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * @desc : 查询权限
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class QueryPermissionModule {
    @Url("alpha2-web/permission/query")
    public static class Request {
        private String slaveUserId;
        private String robotUserId;

        public Request(String slaveUserId, String robotUserId) {
            this.slaveUserId = slaveUserId;
            this.robotUserId = robotUserId;
        }
    }

    public static class Response {

        /**
         * success : true
         * msg : success
         * resultCode : 200
         * data : {"result":[{"permissionDesc":"视频监控","permissionCode":"Avatar","status":0},{"permissionDesc":"相册","permissionCode":"PhotoAlbum","status":0}]}
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
            private List<RobotPermission> result;

            public List<RobotPermission> getResult() {
                return result;
            }

            public void setResult(List<RobotPermission> result) {
                this.result = result;
            }


        }
    }
}
