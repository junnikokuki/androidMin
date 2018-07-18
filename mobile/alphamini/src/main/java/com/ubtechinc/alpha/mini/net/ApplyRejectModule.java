package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * 拒绝账号申请
 */
@Keep
public class ApplyRejectModule {

    @Url("alpha2-web/notice/reject")
    @Keep
    public class Request {

        String noticeId;

        public String getNoticeId() {
            return noticeId;
        }

        public void setNoticeId(String noticeId) {
            this.noticeId = noticeId;
        }
    }

    @Keep
    public class Response extends BaseResponse {

    }

}
