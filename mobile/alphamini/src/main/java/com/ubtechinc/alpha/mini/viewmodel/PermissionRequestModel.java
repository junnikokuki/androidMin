package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.net.RequestPermissionModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * Created by junsheng.chen on 2018/7/3.
 */
public class PermissionRequestModel {

    private volatile static PermissionRequestModel instance;

    public static PermissionRequestModel getInstance() {
        if (instance == null) {
            synchronized (PermissionRequestModel.class) {
                if (instance == null) {
                    instance = new PermissionRequestModel();
                }
            }
        }
        return instance;
    }

    private PermissionRequestModel() {

    }

    public LiveResult<RequestPermissionModule.Response> applyPermission(String robotUserId, String code) {
        final LiveResult liveResult = new LiveResult();
        RequestPermissionModule.Request request = new RequestPermissionModule().new Request();
        request.setRobotUserId(robotUserId);
        request.setCode(code);
        HttpProxy.get().doPost(request, new ResponseListener<RequestPermissionModule.Response>() {
            @Override
            public void onSuccess(RequestPermissionModule.Response response) {
                if (response.isSuccess()) {
                    liveResult.success(response);
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {

            }
        });

        return liveResult;
    }
}
