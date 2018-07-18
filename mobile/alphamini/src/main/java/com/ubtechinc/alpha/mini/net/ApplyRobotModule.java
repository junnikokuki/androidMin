package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * 申请从账号
 */
@Keep
public class ApplyRobotModule {

    @Url("alpha2-web/notice/applyRobot")
    @Keep
    public class Request {

        String robotUserId;

        String masterPhone;

        public String getRobotUserId() {
            return robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
        }

        public String getMasterPhone() {
            return masterPhone;
        }

        public void setMasterPhone(String masterPhone) {
            this.masterPhone = masterPhone;
        }
    }

    @Keep
    public class Response extends BaseResponse {

    }

}
