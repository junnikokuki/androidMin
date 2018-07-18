package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/14
 * 手机绑定
 */

@Keep
public class MobileNumberBindModule {

    @Url("user-service-rest/v2/user/account/bind/extra")
    @Keep
    public static class Request {


        /**
         * account : string
         * accountType : 0
         * appId : 0
         * captcha : string
         */

        private String account;
        private int accountType;
        private int appId;
        private String captcha;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public int getAccountType() {
            return accountType;
        }

        public void setAccountType(int accountType) {
            this.accountType = accountType;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }

    @Keep
    public class Response {
        /**
         * countryCode : string
         * countryName : string
         * emailVerify : 0
         * nickName : string
         * pwdCreateType : 0
         * userBirthday : string
         * userEmail : string
         * userGender : 0
         * userId : 0
         * userImage : string
         * userName : string
         * userPhone : string
         */

        private String countryCode;
        private String countryName;
        private int emailVerify;
        private String nickName;
        private int pwdCreateType;
        private String userBirthday;
        private String userEmail;
        private int userGender;
        private int userId;
        private String userImage;
        private String userName;
        private String userPhone;

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public int getEmailVerify() {
            return emailVerify;
        }

        public void setEmailVerify(int emailVerify) {
            this.emailVerify = emailVerify;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getPwdCreateType() {
            return pwdCreateType;
        }

        public void setPwdCreateType(int pwdCreateType) {
            this.pwdCreateType = pwdCreateType;
        }

        public String getUserBirthday() {
            return userBirthday;
        }

        public void setUserBirthday(String userBirthday) {
            this.userBirthday = userBirthday;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public int getUserGender() {
            return userGender;
        }

        public void setUserGender(int userGender) {
            this.userGender = userGender;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }
    }

}
