package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 设置新的代表动作
 */

public class ActionRepresentiveUpdataModule {
    @Url("alpha2-web/recommendaction/update")
    @Keep
    public class Request {
        private String robotUserId;
        private int actionId;

        public int getAction() {
            return actionId;
        }

        public void setAction(int actionId) {
            this.actionId = actionId;
        }

        public String getRobotUserId() {
            return robotUserId;
        }

        public void setRobotUserId(String robotUserId) {
            this.robotUserId = robotUserId;
        }
    }

    @Keep
    public class Response extends BaseResponse {


    }
}
