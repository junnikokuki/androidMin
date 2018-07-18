package com.ubtechinc.alpha.mini.repository;

import android.os.BatteryManager;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.CmQueryPowerData;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.PowerValue;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * Created by ubt on 2018/3/17.
 */

public class RobotPowerRepository {

    public static final String TAG = "RobotPowerRepository";

    public void getPower(String robotId, final GetPowerCallback callback){
        CmQueryPowerData.CmQueryPowerDataRequest.Builder builder = CmQueryPowerData.CmQueryPowerDataRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_QUERY_ROBOT_POWER_REQUEST,
                IMCmdId.IM_VERSION, builder.build(), robotId , new ICallback<CmQueryPowerData.CmQueryPowerDataResponse>() {
                    @Override
                    public void onSuccess(CmQueryPowerData.CmQueryPowerDataResponse cmQueryPowerDataResponse) {
                        LogUtils.d(TAG, "IM_QUERY_ROBOT_POWER_REQUEST success %s" , cmQueryPowerDataResponse);
                        if(callback != null){
                            PowerValue powerValue = new PowerValue();
                            powerValue.value = cmQueryPowerDataResponse.getPowerValue();
                            powerValue.isCharging = (cmQueryPowerDataResponse.getStatu() == BatteryManager.BATTERY_PLUGGED_AC);
                            powerValue.isLowPower = cmQueryPowerDataResponse.getLevelStatus() == 0;
                            callback.onSuccess(powerValue);
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        LogUtils.d(TAG, "IM_QUERY_ROBOT_POWER_REQUEST onError %s" , e.getMessage());
                        if(callback != null){
                            callback.onError();
                        }
                    }
                });
    }

    public interface GetPowerCallback{

        public void onSuccess(PowerValue powerValue);

        public void onError();

    }
}
