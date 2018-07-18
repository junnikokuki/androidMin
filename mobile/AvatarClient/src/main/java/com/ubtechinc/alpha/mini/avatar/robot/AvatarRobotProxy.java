package com.ubtechinc.alpha.mini.avatar.robot;

import android.util.Log;

import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.mini.avatar.TimerManager;
import com.ubtechinc.alpha.mini.avatar.entity.AvatarChannelInfo;

import java.util.TimerTask;

public class AvatarRobotProxy implements IAvatarRobot {

    public static final String TAG  = "AvatarRobotProxy";

    private IAvatarRobot iAvatarRobot;

    private GetChannelIdCallback getChannelIdCallback;

    private TimerTask getChannelIdTask;

    private int getChannelIdRetryNum = 3;

    public AvatarRobotProxy(IAvatarRobot iAvatarRobot) {
        this.iAvatarRobot = iAvatarRobot;
    }


    @Override
    public boolean isRobotRegist() {
        return iAvatarRobot.isRobotRegist();
    }

    @Override
    public boolean isOffline() {
        return iAvatarRobot.isOffline();
    }

    @Override
    public boolean isOnline() {
        return iAvatarRobot.isOnline();
    }

    @Override
    public boolean isConnected() {
        return iAvatarRobot.isConnected();
    }

    @Override
    public String avatarImUserId() {
        return iAvatarRobot.avatarImUserId();
    }

    @Override
    public String userId() {
        return iAvatarRobot.userId();
    }

    @Override
    public void requestPowerStatus(GetPowerCallback callback) {
        iAvatarRobot.requestPowerStatus(callback);
    }

    @Override
    public void getChannelId(GetChannelIdCallback callback) {
        getChannelIdCallback = callback;
        startGetChannelIdTask();
    }

    @Override
    public void onRobotEvent(OnRobotEventListener listener) {
        iAvatarRobot.onRobotEvent(listener);
    }

    @Override
    public void clearRobotEventListener(OnRobotEventListener listener) {
        iAvatarRobot.clearRobotEventListener(listener);
    }

    @Override
    public void sendControlMessage(AvatarControl.ControlRequest.Builder builder) {
        iAvatarRobot.sendControlMessage(builder);
    }

    @Override
    public void sendControlMessage(AvatarControl.ControlRequest.Builder builder, IMsgCallBack callBack) {
        iAvatarRobot.sendControlMessage(builder, callBack);
    }

    public void startGetChannelIdTask() {
        if (getChannelIdTask == null) {
            getChannelIdTask = new TimerTask() {
                @Override
                public void run() {
                    getChannelIdRetryNum--;
                    if(getChannelIdRetryNum <= 0){ //超时无返回次数超过重试次数返回错误
                        stopGetChannelIdTask();
                        getChannelIdRetryNum = 3;
                        if (getChannelIdCallback != null) {
                            getChannelIdCallback.onError(500,"");
                        }
                    }else {
                        iAvatarRobot.getChannelId(new GetChannelIdCallback() {
                            @Override
                            public void onSuccess(AvatarChannelInfo info) {
                                stopGetChannelIdTask();
                                getChannelIdRetryNum = 3;
                                if (getChannelIdCallback != null) {
                                    getChannelIdCallback.onSuccess(info);
                                }
                            }

                            @Override
                            public void onError(int code, String errorMsg) {
                                if(code == ERROR_BESSY || code == ERROR_BESSY_AVATAR){ //skill被拒绝
                                    stopGetChannelIdTask();
                                    if (getChannelIdCallback != null) {
                                        getChannelIdCallback.onError(code, errorMsg);
                                        getChannelIdCallback = null;
                                    }
                                    getChannelIdRetryNum = 3;
                                }else{
                                    if (getChannelIdRetryNum <= 0) {
                                        stopGetChannelIdTask();
                                        if (getChannelIdCallback != null) {
                                            getChannelIdCallback.onError(code, errorMsg);
                                            getChannelIdCallback = null;
                                        }
                                        getChannelIdRetryNum = 3;
                                    }
                                }

                            }
                        });
                    }
                }
            };
            TimerManager.getInstance().schedule(getChannelIdTask, 0, 15000);
            Log.d(TAG,"startGetChannelIdTask");
        }
    }

    public void stopGetChannelIdTask() {
        if (getChannelIdTask != null) {
            getChannelIdTask.cancel();
            getChannelIdTask = null;
            Log.d(TAG,"stopGetChannelIdTask");
        }
    }

    @Override
    public void shutdown() {
        if (getChannelIdTask != null) {
            getChannelIdTask.cancel();
        }
    }
}
