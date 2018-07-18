package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/14
 * 获取验证码
 */
@Keep
public class GetVetificationModule {


    @Url("user-service-rest/v2/user/captcha")
    @Keep
    public static class Request {
        private String account;

        private String accountType;

        private int purpose;

        private int appId;


        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public int getPurpose() {
            return purpose;
        }

        public void setPurpose(int purpose) {
            this.purpose = purpose;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }
    }



    @Keep
    public class Response {

        /**
         * code : 0
         * message : string
         */

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


}
