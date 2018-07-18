package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * 同意申请
 */
@Keep
public class ApplyApproveModule {

    @Url("alpha2-web/notice/approve")
    @Keep
    public class Request {

        String noticeId;

        String permissions;

        public String getNoticeId() {
            return noticeId;
        }

        public void setNoticeId(String noticeId) {
            this.noticeId = noticeId;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }
    }

    @Keep
    public class Response extends BaseResponse {

    }

}
