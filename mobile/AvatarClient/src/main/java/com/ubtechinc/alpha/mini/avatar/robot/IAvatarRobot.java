package com.ubtechinc.alpha.mini.avatar.robot;


import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.mini.avatar.entity.AvatarChannelInfo;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.List;

public interface IAvatarRobot {

    public static final int ERROR_BESSY = 1;
    public static final int ERROR_BESSY_AVATAR = 2;


    public boolean isRobotRegist();

    public boolean isOffline();

    public boolean isOnline();

    public boolean isConnected();

    public String avatarImUserId();

    public String userId();

    public void requestPowerStatus(GetPowerCallback callback);

    public void getChannelId(GetChannelIdCallback callback);

    public void onRobotEvent(OnRobotEventListener listener);

    public void clearRobotEventListener(OnRobotEventListener listener);

    public void sendControlMessage(AvatarControl.ControlRequest.Builder builder);

    public void sendControlMessage(AvatarControl.ControlRequest.Builder builder, IMsgCallBack callBack);

    public interface GetPowerCallback{

        public void onSuccess(int power);

        public void onError();

    }

    public interface GetChannelIdCallback{

        public void onSuccess(AvatarChannelInfo info);
        public void onError(int code,String errorMsg);

    }

    public interface OnRobotEventListener{

        public void onDisconnect();

        public void onAvatarStopped(int reasonCode, String reason);

        public void onRobotFaildown();

        public void onUserListsChange(List<String> allUsers, List<String> enterUsers, List<String> leaveUsers);

        public void onLowPowerTips();

    }

    public interface IMsgCallBack{
        void onSucces(AlphaMessageOuterClass.AlphaMessage data);

        void onError(ThrowableWrapper e);
    }



    public void shutdown();

}
