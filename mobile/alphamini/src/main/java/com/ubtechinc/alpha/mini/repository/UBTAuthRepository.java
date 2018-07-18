package com.ubtechinc.alpha.mini.repository;

import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.mini.component.http.CookieInterceptor;
import com.ubtechinc.alpha.mini.tvs.entity.LoginInfo;
import com.ubtechinc.alpha.mini.entity.UserInfo;
import com.ubtechinc.alpha.mini.net.LoginoutModule;
import com.ubtechinc.alpha.mini.net.ThirdPartLoginModuleV2;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * Created by ubt on 2017/9/30.
 */

public class UBTAuthRepository {
    public static final String TAG = "UBTAuthRepository";

    public static final int UBT_APP_ID = 100020011;//为了后台统计不同产品的注册数据，增肌一个字段ubtAppId

    public void login(LoginInfo info, final UBTAuthCallBack callback) {
        final ThirdPartLoginModuleV2.LoginRequest loginRequest = new ThirdPartLoginModuleV2().new LoginRequest();
        loginRequest.setOpenId(info.getOpenId());
        loginRequest.setLoginType(info.getLoginType());
        loginRequest.setMiniTvsId(info.getMiniTvsId());
        loginRequest.setAccessToken(info.getAccessToken());
        loginRequest.setAppId(info.getAppId());
        loginRequest.setUbtAppId(UBT_APP_ID);//为了后台统计不同产品的注册数据，增肌一个字段ubtAppId
        HttpProxy.get().doPut(loginRequest, new ResponseListener<ThirdPartLoginModuleV2.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError();
                Logger.e(TAG, "", e);
            }

            @Override
            public void onSuccess(ThirdPartLoginModuleV2.Response loginResponse) {
                CookieInterceptor.get().setToken(loginResponse.getToken().getToken());
                CookieInterceptor.get().setExpireAt(loginResponse.getToken().getExpireAt());
                callback.onSuccess(loginResponse.getUser());
            }
        });
    }

    public void logout(final UBTAuthCallBack callback) {
        LoginoutModule.Request request = new LoginoutModule().new Request();
        HttpProxy.get().doDelete(request, new ResponseListener<LoginoutModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onLogoutError();
                }
            }

            @Override
            public void onSuccess(LoginoutModule.Response response) {
                if (callback != null) {
                    callback.onLogout();
                }
            }
        });
    }

    public interface UBTAuthCallBack {

        public void onSuccess(UserInfo userInfo);

        public void onError();

        public void onLogout();

        public void onLogoutError();
    }
}
