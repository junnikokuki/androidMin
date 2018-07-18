package com.ubtechinc.alpha.mini.repository;


import com.ubtechinc.alpha.mini.repository.datasource.IAccountApplyDataSource;
import com.ubtechinc.alpha.mini.net.ApplyApproveModule;
import com.ubtechinc.alpha.mini.net.ApplyRejectModule;
import com.ubtechinc.alpha.mini.net.ApplyRobotModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

public class AccountApplyRepository implements IAccountApplyDataSource {

    @Override
    public void applyRobot(String robotUserId, final AccountApplyCallback callback) {
        ApplyRobotModule.Request request = new ApplyRobotModule().new Request();

        request.setRobotUserId(robotUserId);
        HttpProxy.get().doPost(request, new ResponseListener<ApplyRobotModule.Response>() {
            @Override
            public void onSuccess(ApplyRobotModule.Response response) {
                if (response.isSuccess()) {
                    callbackSuccess(callback);
                } else {
                    callbackError(callback,response.getResultCode(),response.getMsg());
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                callbackError(callback,e.getErrorCode(),e.getMessage());
            }
        });
    }

    @Override
    public void applyApprove(String noticeId,String permission, final AccountApplyCallback callback) {
        ApplyApproveModule.Request request = new ApplyApproveModule().new Request();
        request.setNoticeId(noticeId);
        request.setPermissions(permission);
        HttpProxy.get().doPost(request, new ResponseListener<ApplyRobotModule.Response>() {
            @Override
            public void onSuccess(ApplyRobotModule.Response response) {
                if (response.isSuccess()) {
                    callbackSuccess(callback);
                } else {
                    callbackError(callback,response.getResultCode(),response.getMsg());
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                callbackError(callback,e.getErrorCode(),e.getMessage());
            }
        });
    }

    @Override
    public void applyReject(String noticeId, final AccountApplyCallback callback) {
        ApplyRejectModule.Request request = new ApplyRejectModule().new Request();
        request.setNoticeId(noticeId);
        HttpProxy.get().doPost(request, new ResponseListener<ApplyRobotModule.Response>() {
            @Override
            public void onSuccess(ApplyRobotModule.Response response) {
                if (response.isSuccess()) {
                    callbackSuccess(callback);
                } else {
                    callbackError(callback,response.getResultCode(),response.getMsg());
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                callbackError(callback,e.getErrorCode(),e.getMessage());
            }
        });
    }


    private void callbackSuccess(AccountApplyCallback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }


    private void callbackError(AccountApplyCallback callback,int code,String msg) {
        if (callback != null) {
            callback.onError(code,msg);
        }
    }
}
