package com.ubtechinc.alpha.mini.net;

import android.support.annotation.Keep;

import com.ubtechinc.nets.http.Url;

/**
 * @ClassName LoginModule
 * @date 6/3/2017
 * @author tanghongyu
 * @Description 登出请求和返回处理
 * @modifier
 * @modify_time
 */
@Keep
public class LoginoutModule {
    @Url("user-service-rest/v2/user/logout")
    @Keep
    public class Request {
    }

    @Keep
    public class Response {

    }

}
