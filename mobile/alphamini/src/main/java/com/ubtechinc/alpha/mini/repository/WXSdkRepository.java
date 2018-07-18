package com.ubtechinc.alpha.mini.repository;

import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.mini.component.http.HttpUtils;
import com.ubtechinc.alpha.mini.repository.datasource.IWXSdkDataSource;
import com.ubtechinc.alpha.mini.entity.WeiXinUserInfo;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

public class WXSdkRepository {

    public static final String TAG = "WXSdkRepository";

    public void getWeiXinUserInfo(String accessToken, String openId, final IWXSdkDataSource.GetWeixinUserInfoCallback callback) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
        HttpUtils.get().doGet(url, new ResponseListener<WeiXinUserInfo>() {


            @Override
            public void onError(ThrowableWrapper e) {
                Logger.w(TAG, "get weixin userinfo error %s", e.getMessage());
                callback.onDataNotAvailable();
            }

            @Override
            public void onSuccess(WeiXinUserInfo userInfo) {
                callback.onLoadUserData(userInfo);
            }
        });
    }
}
