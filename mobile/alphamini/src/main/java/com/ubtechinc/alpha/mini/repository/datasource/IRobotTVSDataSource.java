package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * Created by ubt on 2017/9/30.
 */

public interface IRobotTVSDataSource {

    public void sendTVSAccessTokenData(String accessToken, String freshToken, Long expireTime, String clientId, TVSSendAccessTokenCallback callback);

    public void getTVSProductId(GetTVSProductIdCallBack callBack);

    interface TVSSendAccessTokenCallback {

        void onSuccess();

        void onFail(ThrowableWrapper e);
    }

    interface GetTVSProductIdCallBack {

        void onSuccess(String productId, String dns);

        void onFail(ThrowableWrapper e);
    }

}
