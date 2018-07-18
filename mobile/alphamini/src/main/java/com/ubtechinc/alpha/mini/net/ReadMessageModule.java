package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * Created by ubt on 2018/2/13.
 */

public class ReadMessageModule {


    @Url("alpha2-web/notice/read")
    @Keep
    public class Request {

        private String noticeIds;

        public String getNoticeIds() {
            return noticeIds;
        }

        public void setNoticeIds(String noticeIds) {
            this.noticeIds = noticeIds;
        }
    }

    @Keep
    public class Response extends BaseResponse {

    }


}
