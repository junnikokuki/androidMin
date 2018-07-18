package com.ubtechinc.alpha.mini.component.http;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static class CookieInterceptorHolder {
        private static AuthInterceptor instance = new AuthInterceptor();
    }

    public static AuthInterceptor get() {
        return CookieInterceptorHolder.instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        Response response = chain.proceed(request);
        if (response.code() == 401 || response.code() == 403) {
            if (!url.contains("user/logout")) {
                if (AuthLive.getInstance().getState() == AuthLive.AuthState.LOGINED) {
                    AuthLive.getInstance().forbidden();
                }
            }
        }
        return response;
    }

}
