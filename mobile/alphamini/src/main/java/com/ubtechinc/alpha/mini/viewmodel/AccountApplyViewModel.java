package com.ubtechinc.alpha.mini.viewmodel;

import android.util.Log;

import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.repository.AccountApplyRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IAccountApplyDataSource;

/**
 * 账号申请及申请处理业务逻辑.
 */
public class AccountApplyViewModel {

    public static final String TAG = "AccountApplyViewModel";

    private volatile static AccountApplyViewModel instance;
    private static Object lock = new Object();

    private AccountApplyRepository accountApplyRepository;

    public static AccountApplyViewModel getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AccountApplyViewModel();
                }
            }
        }
        return instance;
    }

    private AccountApplyViewModel() {
        accountApplyRepository = new AccountApplyRepository();
    }

    public LiveResult applyRobot(String robotUserId) {
        final LiveResult liveResult = new LiveResult();
        Log.d(TAG, "applyRobot " + robotUserId);
        accountApplyRepository.applyRobot(robotUserId, new IAccountApplyDataSource.AccountApplyCallback() {
            @Override
            public void onSuccess() {
                liveResult.success(null);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code,msg);
            }
        });
        return liveResult;
    }

    public LiveResult applyApprove(final Message message, final String permission) {
        final LiveResult liveResult = new LiveResult();
        Log.d(TAG, "applyApprove " + message + " permission = " + permission);
        accountApplyRepository.applyApprove(message.getNoticeId(), permission,new IAccountApplyDataSource.AccountApplyCallback() {
            @Override
            public void onSuccess() {
                message.setOperation(String.valueOf(Message.OP_ACCEPT));
                liveResult.success("");
            }

            @Override
            public void onError(int code,String msg) {
                liveResult.fail(code,msg);
            }
        });
        return liveResult;
    }

    public LiveResult applyReject(final Message message) {
        Log.d(TAG, "applyReject " + message);
        final LiveResult liveResult = new LiveResult();
        accountApplyRepository.applyReject(message.getNoticeId(), new IAccountApplyDataSource.AccountApplyCallback() {
            @Override
            public void onSuccess() {
                message.setOperation(String.valueOf(Message.OP_REJECT));
                liveResult.success(null);
            }

            @Override
            public void onError(int code,String msg) {
                liveResult.fail(code,msg);
            }
        });
        return liveResult;
    }

}
