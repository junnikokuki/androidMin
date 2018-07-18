package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.nets.http.Url;

import java.util.List;

/**
 * 获取消息
 */
@Keep
public class GetMsgModule {
    @Url("alpha2-web/notice/query")
    @Keep
    public class Request {

        int pagesize;

        int page;

        int noticeType;

        public int getPagesize() {
            return pagesize;
        }

        public void setPagesize(int pagesize) {
            this.pagesize = pagesize;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(int noticeType) {
            this.noticeType = noticeType;
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

        private List<Message> systemResult;

        private List<Message> shareResult;

        public List<Message> getSystemResult() {
            return systemResult;
        }

        public void setSystemResult(List<Message> systemResult) {
            this.systemResult = systemResult;
        }

        public List<Message> getShareResult() {
            return shareResult;
        }

        public void setShareResult(List<Message> shareResult) {
            this.shareResult = shareResult;
        }
    }

}
