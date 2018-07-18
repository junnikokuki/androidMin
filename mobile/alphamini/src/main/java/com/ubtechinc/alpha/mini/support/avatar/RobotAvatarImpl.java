package com.ubtechinc.alpha.mini.support.avatar;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.avatar.common.Constants;
import com.ubtechinc.alpha.mini.avatar.entity.AvatarChannelInfo;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.AvatarRobotFailDownEvent;
import com.ubtechinc.alpha.mini.event.AvatarRobotLowPowerTipsEvent;
import com.ubtechinc.alpha.mini.event.AvatarStoppedEvent;
import com.ubtechinc.alpha.mini.event.AvatarUserListsChangeEvent;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.repository.RobotAvatarRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAvatarSource;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class RobotAvatarImpl implements IAvatarRobot {

    public static final String TAG = "RobotAvatarImpl";

    private static RobotAvatarImpl instance;

    OnRobotEventListener onRobotEventListener;

    private String robotNum;

    private RobotAvatarImpl() {

    }

    public static RobotAvatarImpl getInstance() {
        if (instance == null) {
            synchronized (RobotAvatarImpl.class) {
                if (instance == null) {
                    instance = new RobotAvatarImpl();
                    EventBus.getDefault().register(instance);
                }
            }
        }
        return instance;
    }

    public static void onDestroy() {
        if (instance != null) {
            EventBus.getDefault().unregister(instance);
            instance = null;
        }
    }


    @Override
    public boolean isOffline() {

        return false;
    }

    @Override
    public boolean isOnline() {
        return true; //TODO
    }

    @Override
    public boolean isConnected() {
        return true; //TODO
    }

    @Override
    public String avatarImUserId() {
        robotNum = MyRobotsLive.getInstance().getRobotUserId();
        return robotNum + "ilive";
    }

    @Override
    public String userId() {
        return AuthLive.getInstance().getUserId();
    }

    @Override
    public void requestPowerStatus(final GetPowerCallback callback) {
        /*
        RobotInitRepository.getInstance().getPower(new IRobotInitDataSource.GetPowerDataCallback() {
            @Override
            public void onLoadPowerData(int power) {
                if (callback != null) {
                    callback.onSuccess(power);
                }
            }

            @Override
            public void onDataNotAvailable(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onError();
                }
            }
        });*/
    }

    @Override
    public void getChannelId(final GetChannelIdCallback callback) {
        robotNum = MyRobotsLive.getInstance().getRobotUserId();
        if (robotNum != null) {
            doGetChannelId(callback);
        }else{
            callback.onError(500,"");
        }
    }

    private void doGetChannelId(final GetChannelIdCallback callback) {
        RobotAvatarRepository.getInstance().startAvatar(new IRobotAvatarSource.StartAvatarCallback() {
            @Override
            public void onSuccess(String channelId, String channelKey) {
                if (callback != null) {
                    AvatarChannelInfo info = new AvatarChannelInfo();
                    info.setChannelId(channelId);
                    info.setChannelKey(channelKey);
                    callback.onSuccess(info);
                }
            }

            @Override
            public void onFail(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }


    @Override
    public void onRobotEvent(OnRobotEventListener listener) {
        onRobotEventListener = listener;
    }

    @Override
    public void clearRobotEventListener(OnRobotEventListener listener) {
        onRobotEventListener = null;
    }

    @Override
    public void sendControlMessage(final AvatarControl.ControlRequest.Builder builder) {
        sendControlMessage(builder, null);
    }

    public void sendControlMessage(final AvatarControl.ControlRequest.Builder builder, final IMsgCallBack callBack){
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_AVATAR_CONTROL_REQUEST,
                IMCmdId.IM_VERSION, builder.build(), robotId + "ilive", new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
                    @Override
                    public void onSuccess(AlphaMessageOuterClass.AlphaMessage request) {
                        Log.i(TAG, "onSuccess  sendControlMessage" + builder.getType());
                        if (callBack != null){
                            callBack.onSucces(request);
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.e(TAG, "onError: sendControlMessage" + builder.getType());
                        if (callBack != null){
                            callBack.onError(e);
                        }
                    }
                });
    }




    @Override
    public boolean isRobotRegist() {
        List<RobotInfo> list = MyRobotsLive.getInstance().getRobots().getData();
        return list != null && list.size() != 0;
    }


    @Subscribe
    public void onEvent(AvatarStoppedEvent event) {
        if (onRobotEventListener != null) {
            onRobotEventListener.onAvatarStopped(event.getReasonCode(), event.getReason());
        }
    }

    @Subscribe
    public void onEvent(AvatarRobotFailDownEvent event) {
        if (onRobotEventListener != null) {
            onRobotEventListener.onRobotFaildown();
        }
    }

    @Subscribe
    public void onEvent(AvatarRobotLowPowerTipsEvent event) {
        if (onRobotEventListener != null) {
            onRobotEventListener.onLowPowerTips();
        }
    }

    @Subscribe
    public void onEvent(AvatarUserListsChangeEvent event) {
        if (onRobotEventListener != null) {
            onRobotEventListener.onUserListsChange(event.getAllUsers(), event.getEnterUserIds(), event.getLeaveUserIds());
        }
    }

    @Override
    public void shutdown() {
        instance = null;
        if (onRobotEventListener == null) {
            onRobotEventListener = null;
        }
    }
}
