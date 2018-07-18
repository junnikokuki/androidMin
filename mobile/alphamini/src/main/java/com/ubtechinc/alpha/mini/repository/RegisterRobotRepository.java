package com.ubtechinc.alpha.mini.repository;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @author：wululin
 * @date：2017/10/30 10:08
 * @modifier：ubt
 * @modify_date：2017/10/30 10:08
 * [A brief description]
 * version
 */

public class RegisterRobotRepository {
    private static final String TAG = RegisterRobotRepository.class.getSimpleName();
    public void registerRobot(String userName, String userOnlyId, final RegisterRobotListener listener){
        Log.d(TAG, " registerRobot userName : " + userName);
        RegisterRobotModule.Request request = new RegisterRobotModule().new Request();
        request.setUserName(userName);
        request.setUserOnlyId(userOnlyId);

        HttpProxy.get().doPost(request, new ResponseListener<RegisterRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG, " registerRobot onError : ");
                Logger.e(TAG,"e====",e);
                listener.onError();
            }

            @Override
            public void onSuccess(RegisterRobotModule.Response response) {
                Log.d(TAG, " registerRobot onSuccess : ");
                listener.onSuccess(response);
            }
        });
    }

    public interface RegisterRobotListener{
        public void onSuccess(RegisterRobotModule.Response response);

        public void onError();
    }
}
