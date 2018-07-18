package com.ubtect.xingepushlibrary.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;
import com.ubtect.xingepushlibrary.AppInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * @Date: 2017/10/31.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 获取推送App信息
 */
@Keep
public class GetPushAppInfoModule {
    @Url("xinge-push-rest/push/appInfo")
    @Keep
    public class Request {
        String appName;

        public void setAppName(String appName) {
            this.appName = appName;
        }
    }


}
