package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * Created by ubt on 2018/2/13.
 */

public class ReadAllMessageModule {


    @Url("alpha2-web/notice/readAll")
    @Keep
    public class Request {

        private int noticeType;

        public int getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(int noticeType) {
            this.noticeType = noticeType;
        }
    }

    @Keep
    public class Response extends BaseResponse {

    }


}
