package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @作者：liudongyang
 * @日期: 18/6/26 14:39
 * @描述:
 */
public class UpdateInfoModule {

    @Url("v1/upgrade-rest/version/upgradable")
    @Keep
    public static class Request {
        private String productName;
        private String moduleNames;
        private String versionNames;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getModuleNames() {
            return moduleNames;
        }

        public void setModuleNames(String moduleNames) {
            this.moduleNames = moduleNames;
        }

        public String getVersionNames() {
            return versionNames;
        }

        public void setVersionNames(String versionNames) {
            this.versionNames = versionNames;
        }
    }

    @Keep
    public static class AppUpdateModule {

        /**
         * moduleName : AndroidApp
         * versionName : v1.0.2.2
         * isIncremental : false
         * packageUrl : http://ooy006vgr.bkt.clouddn.com/upgrade/2018/06/1530255277197/alphamini-v1.0.2.2.apk
         * packageMd5 : fb0be3f8b34eaece7901c1dc227bad2d
         * packageSize : 75818516
         * isForced : false
         * releaseNote : 1. 修复首页异常Crash；
         2. 修复相册功能；
         3. 修复联系人号码-问题
         * releaseTime : 1530255662
         */

        private String moduleName;
        private String versionName;
        private boolean isIncremental;
        private String packageUrl;
        private String packageMd5;
        private int packageSize;
        private boolean isForced;
        private String releaseNote;
        private long releaseTime;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public boolean isIsIncremental() {
            return isIncremental;
        }

        public void setIsIncremental(boolean isIncremental) {
            this.isIncremental = isIncremental;
        }

        public String getPackageUrl() {
            return packageUrl;
        }

        public void setPackageUrl(String packageUrl) {
            this.packageUrl = packageUrl;
        }

        public String getPackageMd5() {
            return packageMd5;
        }

        public void setPackageMd5(String packageMd5) {
            this.packageMd5 = packageMd5;
        }

        public int getPackageSize() {
            return packageSize;
        }

        public void setPackageSize(int packageSize) {
            this.packageSize = packageSize;
        }

        public boolean isIsForced() {
            return isForced;
        }

        public void setIsForced(boolean isForced) {
            this.isForced = isForced;
        }

        public String getReleaseNote() {
            return releaseNote;
        }

        public void setReleaseNote(String releaseNote) {
            this.releaseNote = releaseNote;
        }

        public long getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(long releaseTime) {
            this.releaseTime = releaseTime;
        }

        @Override
        public String toString() {
            return "AppUpdateModule{" +
                    "moduleName='" + moduleName + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", isIncremental=" + isIncremental +
                    ", packageUrl='" + packageUrl + '\'' +
                    ", packageMd5='" + packageMd5 + '\'' +
                    ", packageSize=" + packageSize +
                    ", isForced=" + isForced +
                    ", releaseNote='" + releaseNote + '\'' +
                    ", releaseTime=" + releaseTime +
                    '}';
        }
    }




}
