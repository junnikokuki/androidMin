package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class PetphraseUpdataModule {
    @Url("alpha2-web/petphrase/update")
    @Keep
    public class Request {
        private String robotUserId;
        private String petPhrase;
        public String getPetPhrase() {
            return petPhrase;
        }

        public void setPetPhrase(String petPhrase) {
            this.petPhrase = petPhrase;
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
