package com.ubtechinc.alpha.mini.repository;

import android.util.Log;

import com.ubtechinc.alpha.CmModifyMobileDataStatus;
import com.ubtechinc.alpha.CmModifyMobileRoamStatus;
import com.ubtechinc.alpha.CmQueryMobileNetwork;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * 蜂窝数据网
 * <p>
 * Created by ubt on 2018/2/5.
 */

public class CellularDataRepository {

    public static final String TAG = "CellularDataRepository";

    public void getState(String robotId, final GetStateCallback callback) {
        CmQueryMobileNetwork.CmQueryMobileNetworkRequest.Builder builder = CmQueryMobileNetwork.CmQueryMobileNetworkRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_MOBILE_NET_STATE_REQUEST,
                IMCmdId.IM_VERSION, builder.build(), robotId, new ICallback<CmQueryMobileNetwork.CmQueryMobileNetworkResponse>() {
                    @Override
                    public void onSuccess(CmQueryMobileNetwork.CmQueryMobileNetworkResponse data) {
                        Log.d(TAG,"getState success" + data.toString());
                        if (callback != null) {
                            callback.onSuccess(data.getIsOpenData(), data.getIsOpenRoam());
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.d(TAG,"getState onError" + e.toString());
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.getMessage());
                        }
                    }
                });
    }

    /**
     * 切换蜂窝数据开关
     */
    public void switchDataStatus(String robotId, boolean isOpen, final StateOpCallback callback) {
        CmModifyMobileDataStatus.CmModifyMobileDataStatusRequest.Builder builder = CmModifyMobileDataStatus.CmModifyMobileDataStatusRequest.newBuilder();
        builder.setIsOpen(isOpen);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_MOBILE_NET_MODIFY_REQUEST,
                IMCmdId.IM_VERSION, builder.build(), robotId, new ICallback<CmModifyMobileDataStatus.CmModifyMobileDataStatusResponse>() {
                    @Override
                    public void onSuccess(CmModifyMobileDataStatus.CmModifyMobileDataStatusResponse data) {
                        Log.d(TAG,"switchDataStatus success" + data.toString());
                        if (callback != null) {
                            if (data.getIsSuccess()) {
                                callback.onSuccess();
                            } else {
                                callback.onError(data.getResultCode(), "");
                            }
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.d(TAG,"switchDataStatus onError" + e.toString());
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.getMessage());
                        }
                    }
                });
    }


    /**
     * 切换漫游数据开关
     */
    public void switchRoamStatus(String robotId, boolean isOpen, final StateOpCallback callback) {
        CmModifyMobileRoamStatus.CmModifyMobileRoamRequest.Builder builder = CmModifyMobileRoamStatus.CmModifyMobileRoamRequest.newBuilder();
        builder.setIsOpen(isOpen);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_MOBILE_ROAM_MODIFY_REQUEST,
                IMCmdId.IM_VERSION, builder.build(), robotId, new ICallback<CmModifyMobileRoamStatus.CmModifyMobileRoamResponse>() {
                    @Override
                    public void onSuccess(CmModifyMobileRoamStatus.CmModifyMobileRoamResponse data) {
                        Log.d(TAG,"switchRoamStatus success" + data.toString());
                        if (callback != null) {
                            if (data.getIsSuccess()) {
                                callback.onSuccess();
                            } else {
                                callback.onError(data.getResultCode(), "");
                            }
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.d(TAG,"switchRoamStatus onError" + e.toString());
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.getMessage());
                        }
                    }
                });
    }

    public interface StateOpCallback {
        public void onSuccess();

        public void onError(int code, String msg);
    }

    public interface GetStateCallback {
        public void onSuccess(boolean isCellOpened, boolean isRoamingOpened);

        public void onError(int code, String msg);
    }
}
