package com.ubtechinc.alpha.mini.support.tvs;

import com.ubtechinc.alpha.mini.repository.RobotTVSRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotTVSDataSource;
import com.ubtechinc.alpha.mini.tvs.IRobotTvsManager;
import com.ubtechinc.nets.http.ThrowableWrapper;

public class RobotTvsManagerImpl implements IRobotTvsManager {

    private static RobotTvsManagerImpl robotTvsManager;

    public static IRobotTvsManager getInstance() {
        if (robotTvsManager == null) {
            robotTvsManager = new RobotTvsManagerImpl();
        }
        return robotTvsManager;
    }

    private RobotTvsManagerImpl() {

    }

    @Override
    public void getProductId(final GetRobotTvsProductIdListener listener) {

        RobotTVSRepository.getInstance().getTVSProductId(new IRobotTVSDataSource.GetTVSProductIdCallBack() {

            @Override
            public void onSuccess(String productId, String dsn) {
                if (listener != null) {
                    listener.onSuccess(productId, dsn);
                }
            }

            @Override
            public void onFail(ThrowableWrapper e) {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

    @Override
    public void sendAccessToken(String accessToken, String freshToken, Long expireTime,String clientId,final SendTvsAccessTokenListener listener) {
        RobotTVSRepository.getInstance().sendTVSAccessTokenData(accessToken, freshToken, expireTime, clientId, new IRobotTVSDataSource.TVSSendAccessTokenCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(ThrowableWrapper e) {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

}
