package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * 获取未读消息个数
 */
@Keep
public class GetNoReadMsgModule {
    @Url("alpha2-web/notice/queryNoReadCount")
    @Keep
    public class Request {

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

        private int systemMsgCount;
        private int shareMsgCount;

        public int getSystemMsgCount() {
            return systemMsgCount;
        }

        public void setSystemMsgCount(int systemMsgCount) {
            this.systemMsgCount = systemMsgCount;
        }

        public int getShareMsgCount() {
            return shareMsgCount;
        }

        public void setShareMsgCount(int shareMsgCount) {
            this.shareMsgCount = shareMsgCount;
        }
    }

}
