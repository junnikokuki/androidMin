package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * @desc : 机器人从账号模块
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/1
 */

public class RobotSlaveModule {
    @Url("alpha2-web/relation/getSlaveUsers")
    @Keep
    public static class Request {
        String robotUserId = "123456";

        public Request(String robotUserId) {
            this.robotUserId = robotUserId;
        }
    }

    @Keep
    public class Response {

        /**
         * success : true
         * msg : “success”
         * data : {"result":[{"userId":"\u201cxxxx\u201d","userName":"\u201cxxxxx\u201d"},{"userId":"\u201cxxxx\u201d","userName":"\u201cxxxxx\u201d"}]}
         */

        private boolean success;
        private String msg;
        private DataBean data;

        public List<DataBean.ResultBean> getDataBeanList() {
            if(data != null) {
                return data.getResult();
            }
            return null;
        }

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

        @Override
        public String toString() {
            return "Response{" +
                    "success=" + success +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    public static class DataBean {
        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "result=" + result +
                    '}';
        }

        public static class ResultBean {
            /**
             * userId : “xxxx”
             * userName : “xxxxx”
             */

            private String userId;
            private String userName;
            private String userImage;
            private String relationDate;

            public ResultBean(String userId, String userName, String userImage) {
                this.userId = userId;
                this.userName = userName;
                this.userImage = userImage;
            }

            public ResultBean() {
            }

            public String getRelationDate() {
                return relationDate;
            }

            public void setRelationDate(String relationDate) {
                this.relationDate = relationDate;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public void setUserImage(String userImage) {
                this.userImage = userImage;
            }

            public String getUserImage() {
                return userImage;
            }

            @Override
            public String toString() {
                return "ResultBean{" +
                        "userId='" + userId + '\'' +
                        ", userName='" + userName + '\'' +
                        '}';
            }
        }
    }
}
