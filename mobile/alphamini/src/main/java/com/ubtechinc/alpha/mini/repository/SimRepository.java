package com.ubtechinc.alpha.mini.repository;

import android.util.Log;

import com.ubtechinc.alpha.CmCheckSimCard;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.SimState;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * Created by ubt on 2018/2/5.
 */

public class SimRepository {

    public static final String TAG = "SimRepository";

    public void checkSimState(String robotId, final CheckSimStateCallback callback) {
        CmCheckSimCard.CmCheckSimCardRequest.Builder builder =
                CmCheckSimCard.CmCheckSimCardRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CHECK_SIM_REQUEST, IMCmdId.IM_VERSION,
                builder.build(), robotId, new ICallback<CmCheckSimCard.CmCheckSimCardResponse>() {
                    @Override
                    public void onSuccess(CmCheckSimCard.CmCheckSimCardResponse data) {
                        Log.d(TAG, "checkSimCad success " + data.getIsExist() + data.getPhoneNumber());
                        if (callback != null) {
                            callback.onSuccess(new SimState(data.getIsExist(), data.getPhoneNumber()));
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.d(TAG, "checkSimCad error " + e.getErrorCode() + e.getMessage());
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.getMessage());
                        }
                    }
                });
    }

    public interface CheckSimStateCallback {
        public void onSuccess(SimState simState);

        public void onError(int code, String msg);
    }
}
