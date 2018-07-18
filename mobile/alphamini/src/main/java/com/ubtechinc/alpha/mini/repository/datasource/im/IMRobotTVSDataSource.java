package com.ubtechinc.alpha.mini.repository.datasource.im;

import com.orhanobut.logger.Logger;
import com.ubtech.utilcode.utils.thread.HandlerUtils;
import com.ubtechinc.alpha.TVSSendAccessToken;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotTVSDataSource;

import com.ubtechinc.alpha.mini.tvs.TVSManager;

import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.im.IMErrorUtil;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * 通过 IM 通信 与机器人端TVS登录时相关操作.
 */

public class IMRobotTVSDataSource implements IRobotTVSDataSource {

    public static final String product_id = "b0851325-3056-4853-921b-dcba21b491a3:8c901ad100ad44d98b6276adeb861058";

    public static final String TAG = IMRobotTVSDataSource.class.getSimpleName();

    @Override
    public void sendTVSAccessTokenData(String accessToken, String freshToken, Long expireTime, String clientId, final TVSSendAccessTokenCallback callback) {
        TVSSendAccessToken.TVSSendAccessTokenRequest.Builder builder = TVSSendAccessToken.TVSSendAccessTokenRequest.newBuilder();
        TVSSendAccessToken.TVSSendAccessTokenRequest request = builder.setAccessToken(accessToken).
                setFreshToken(freshToken)
                .setExpireTime(expireTime)
                .setClientId(clientId)
                .build();
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        String robotUserId = "";
        if (robotInfo != null) {
            robotUserId = robotInfo.getRobotUserId();
 /*           if (robotInfo.getConnectionState() != RobotInfo.ROBOT_STATE_CONNECTED) {
                if (callback != null) {
                    callback.onFail(null);
                }
                return;
            }*/
        }
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_SEND_TVS_ACCESSTOKEN_REQUEST, "1", request, robotUserId, new ICallback<TVSSendAccessToken.TVSSendAccessTokenResponse>() {

            @Override
            public void onSuccess(final TVSSendAccessToken.TVSSendAccessTokenResponse data) {
                Logger.d(TAG, "sendTvsAccessTokenData, result = %s", data.getIsSuccess());
                if (data.getIsSuccess()) {

                    HandlerUtils.runUITask(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    });

                } else {
                    HandlerUtils.runUITask(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(IMErrorUtil.handleException(data.getResultCode()));
                        }
                    });

                }
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                Logger.w(TAG, "sendTvsAccessTokenData onError ", e.getMessage());
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e);
                    }
                });
            }
        });
    }

    @Override
    public void getTVSProductId(final GetTVSProductIdCallBack callback) {
        Logger.d(TAG, "getTVSProductId");
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (robotInfo != null) {
            final String robotUserId = robotInfo.getRobotUserId();
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {

                        callback.onSuccess(TVSManager.PRODUCT_ID,robotUserId);

                    }
                }
            });
/*            if (robotInfo.getConnectionState() != RobotInfo.ROBOT_STATE_CONNECTED) {
                if (callback != null) {
                    callback.onFail(null);
                }
                return;
            }*/
        } else {
            if (callback != null) {
                callback.onFail(new ThrowableWrapper("robotId is null", 500));
            }
        }
        /*Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_TVS_PRODUCTID_REQUEST, "1",
                null, robotUserId, new ICallback<TVSGetRobotProductId.TVSGetRobotProductIdResponse>() {

                    @Override
                    public void onSuccess(final TVSGetRobotProductId.TVSGetRobotProductIdResponse data) {
                        Logger.d(TAG, "getTVSProductId success : ", data.getProductId(), data.getDsn());
                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(data.getProductId(), data.getDsn());
                            }
                        });
                    }

                    @Override
                    public void onError(final ThrowableWrapper e) {
                        Logger.w("getTVSProductId error : ", e.getMessage());
                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail(IMErrorUtil.handleException(e.getErrorCode()));
                            }
                        });
                    }
                });*/

    }
}
