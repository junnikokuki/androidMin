package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.alpha.mini.entity.WeiXinUserInfo;

public interface IWXSdkDataSource {

    public interface GetWeixinUserInfoCallback {

        void onLoadUserData(WeiXinUserInfo weiXinUserInfo);

        void onDataNotAvailable();

    }
}
