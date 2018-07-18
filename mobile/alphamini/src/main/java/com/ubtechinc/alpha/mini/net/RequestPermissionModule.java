package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * Created by junsheng.chen on 2018/7/3.
 */
@Keep
public class RequestPermissionModule {

    public static final int PERMISSION_SEND_SUCCESS = 0;
    public static final int PERMISSION_SEND_FREQUENT = 1;
    @Keep
    @Url("alpha2-web/notice/applypermission")
    public class Request {

        String robotUserId;

        String code;

        public String getRobotUserId() {
            return robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Keep
    public class Response extends BaseResponse {

        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    @Keep
    public class Data {

        private int INPERIOD;

        public int getINPERIOD() {
            return INPERIOD;
        }

        public void setINPERIOD(int INPERIOD) {
            this.INPERIOD = INPERIOD;
        }
    }
}
