package com.ubtechinc.alpha.mini.net;

import com.ubtechinc.nets.http.Url;

/**
 * @ClassName FeedbackModule
 * @date 7/27/2017
 * @author tanghongyu
 * @Description 用户反馈
 * @modifier
 * @modify_time
 */
public class FeedbackModule {
    @Url("alpha2-web/system/feedback")
    public class Request {
        private String appVersion ;
        private String feedbackInfo;
        private String feedbackUser;
        private int uploadLog;
        private String equipmentId;

        public String getFeedbackUser() {
            return feedbackUser;
        }

        public void setFeedbackUser(String feedbackUser) {
            this.feedbackUser = feedbackUser;
        }


        public String getFeedbackInfo() {
            return feedbackInfo;
        }
        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public void setFeedbackInfo(String feedbackInfo) {
            this.feedbackInfo = feedbackInfo;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setUploadLog(int uploadLog) {
            this.uploadLog = uploadLog;
        }

        public int getUploadLog() {
            return uploadLog;
        }
    }

    public class Response extends BaseResponse {

        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }
    public class Result {


    }
    public class Data {

    }

}
