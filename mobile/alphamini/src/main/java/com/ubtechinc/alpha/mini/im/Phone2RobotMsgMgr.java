package com.ubtechinc.alpha.mini.im;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.protobuf.GeneratedMessageLite;
import com.orhanobut.logger.Logger;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtech.utilcode.utils.notification.Subscriber;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.event.IMLoginResultEvent;
import com.ubtechinc.alpha.im.TecentIMManager;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.nets.im.business.LoginBussiness;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtechinc.nets.phonerobotcommunite.ITimeoutEngine;
import com.ubtechinc.nets.phonerobotcommunite.NetCheckEngine;
import com.ubtechinc.nets.phonerobotcommunite.RobotCommuniteManager;

/**
 * Created by Administrator on 2017/5/26.
 */

public class Phone2RobotMsgMgr<T> {
    private static Phone2RobotMsgMgr sInstance;
    private final static String TAG = "Phone2RobotMsgMgr";
    private IMLoginSubscriber loginSubscriber = new IMLoginSubscriber();
    ImLoginListener imLoginListener;

    private String currentUserId;

    private ImTimeoutEngine imTimeoutEngine;

    public static Phone2RobotMsgMgr getInstance() {
        if (sInstance == null) {
            synchronized (Phone2RobotMsgMgr.class) {
                if (sInstance == null) {
                    sInstance = new Phone2RobotMsgMgr();
                }
            }
        }
        return sInstance;
    }

    public void init(final String userId, ImLoginListener imLoginListener) {
        this.imLoginListener = imLoginListener;
        RobotCommuniteManager.getInstance().init();
        RobotCommuniteManager.getInstance().setMsgDispathcer(new ImPhoneClientMsgDispatcher());
        imTimeoutEngine = ImTimeoutEngine.get();
        imTimeoutEngine.setWhileList(ImTimeoutWhileList.whileList);
        RobotCommuniteManager.getInstance().setTimeoutEngine(imTimeoutEngine);
        RobotCommuniteManager.getInstance().setConnectListioner(connectListener);
        new Handler(Looper.getMainLooper()).post(new Runnable() { //TIMManager要求在主线程或有Looper的线程中初始化
            @Override
            public void run() {
                currentUserId = userId;
                TecentIMManager.getInstance(AlphaMiniApplication.getInstance()).init(userId);
            }
        });
        NotificationCenter.defaultCenter().subscriber(IMLoginResultEvent.class, loginSubscriber);
    }

    public void setTimeoutEngine(ITimeoutEngine timeoutEngine) {
        RobotCommuniteManager.getInstance().setTimeoutEngine(timeoutEngine);
    }

    private class IMLoginSubscriber implements Subscriber<IMLoginResultEvent> {

        @Override
        public void onEvent(IMLoginResultEvent imLoginResultEvent) {
            if (imLoginResultEvent != null && imLoginResultEvent.success) {
                Logger.d(TAG, "IMLogin success");
                if (imLoginListener != null) {
                    imLoginListener.onSuccess();
                    imLoginListener = null;
                }
            } else {
                Logger.i(TAG, "IMLogin fail");
                imLoginListener.onError();
                imLoginListener = null;
            }
        }
    }


    public void sendData(int cmdId, String version, GeneratedMessageLite requestBody, String peer, @NonNull ICallback<AlphaMessageOuterClass.AlphaMessage> dataCallback) {
        RobotCommuniteManager.getInstance().sendData(cmdId, version, requestBody, peer, dataCallback);
    }


    public void sendDataToRobot(int cmdId, String version, GeneratedMessageLite requestBody, String peer, @NonNull ICallback<T> dataCallback) {

        RobotCommuniteManager.getInstance().sendDataToRobot(cmdId, version, requestBody, peer, dataCallback);
    }

    public interface ImLoginListener {
        public void onSuccess();

        public void onError();
    }

    public void release() {
        if (imTimeoutEngine != null) {
            imTimeoutEngine.release();
        }
        if (sInstance != null) {
            sInstance = null;
        }
    }

    private NetCheckEngine.ConnectListener connectListener = new NetCheckEngine.ConnectListener() {

        @Override
        public void onReconnected() {
            LogUtils.d(TAG, "reLogin userId = " + currentUserId);
            if (!TextUtils.isEmpty(currentUserId)) {
                LoginBussiness.getInstance(AlphaMiniApplication.getInstance()).login(currentUserId);
            }
        }

        @Override
        public void onDisconnected() {

        }
    };
}
