package com.ubtechinc.alpha.mini.avatar.rtc;

import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.PermissionManager;
import com.ubtechinc.alpha.mini.avatar.common.PermissionMicRequest;
import com.ubtechinc.alpha.mini.avatar.entity.AvatarChannelInfo;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.avatar.utils.PackActionMessageUtil;
import com.ubtechinc.alpha.mini.avatar.widget.HalfJoyStickView;

import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager.OnAvatarListener.START_AVATAR_ERROR;
import static com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager.Status.INIT;
import static com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager.Status.START_FAIL;

public abstract class IRtcManager {

    public static final String TAG = "RtcManager";

    public enum Status {
        INIT, CHANNEL_REQUEST, CHANNEL_OBTAINED, FIRST_FRAME_OBTAINED, START_FAIL, INIT_ERROR
    }

    protected Status status = INIT;

    protected IAvatarRobot avatarRobot;
    protected OnAvatarListener listener;
    protected Activity context;

    protected String channelId;
    protected String channelKey;

    private GetChannelIdCallback getChannelIdCallback;

    public boolean micEnable = false;

    protected SurfaceView videoView;

    private Timer exitTimer;
    private Timer heartbeatTimer;

    protected int rotation = 0;

    public IRtcManager(Activity context, OnAvatarListener listener, IAvatarRobot avatarRobot) {
        this.context = context;
        this.listener = listener;
        this.avatarRobot = avatarRobot;
        getChannelIdCallback = new GetChannelIdCallback(this);
    }

    public void start() {
        PermissionManager.requestMicPermission(context, new PermissionMicRequest.PermissionMicCallback() {
            @Override
            public void onSuccessful() {
                requestChannelId();
            }

            @Override
            public void onFailure() {
                status = START_FAIL;
                if (listener != null) {
                    listener.onError(IRtcManager.OnAvatarListener.PERMISSION_ERROR, "");
                }
            }

            @Override
            public void onRationSetting() {

            }
        });
    }

    public void start(SurfaceView videoView) {
        start(videoView, 0);
    }

    public void start(SurfaceView videoView, int rotation) {
        this.videoView = videoView;
        this.rotation = rotation;
        Log.d(TAG, "start current status= " + status);
        switch (status) {
            case INIT_ERROR:
                setupRtcEngine(new InitResult() {
                    @Override
                    public void onSuccess() {
                        start();
                    }
                });
                break;
            case INIT:
                start();
                break;
            case CHANNEL_REQUEST:
                break;
            case CHANNEL_OBTAINED:
                break;
            case FIRST_FRAME_OBTAINED:
                setUpRemoteVideo(videoView, rotation);
                break;
        }
    }

    public void restart(SurfaceView videoView, int rotation) {
        if (status == START_FAIL) {
            status = INIT;
            start(videoView, rotation);
        }
    }

    public void stop() {
        sendLevelChannelMessage();
        doStop();
        status = INIT;
        if (listener != null) {
            listener.onAvatarStop();
        }
    }

    public void shutdown() {
        sendLevelChannelMessage();
//        doStop();
        if (getChannelIdCallback != null) {
            getChannelIdCallback.clear();
            getChannelIdCallback = null;
        }
        listener = null;
        stopExitTimer();
        doShutdown();
    }

    protected abstract void setupRtcEngine(InitResult initResult);

    protected abstract void doStop();

    protected abstract void doShutdown();

    public abstract void localStop();

    protected abstract void enableMic(boolean enable);

    public boolean micStatusSwitch() {
        if (micEnable) {
            enableMic(false);
            if (listener != null) {
                listener.onMicStateChange(micEnable);
            }
        } else {
            PermissionManager.requestMicPermission(context, new PermissionMicRequest.PermissionMicCallback() {
                @Override
                public void onSuccessful() {
                    enableMic(true);
                    if (listener != null) {
                        listener.onMicStateChange(micEnable);
                    }
                }

                @Override
                public void onFailure() {
                    if (listener != null) {
                        listener.onMicStateChange(micEnable);
                    }
                }

                @Override
                public void onRationSetting() {
                    PermissionManager.showRationDialog(context);
                }
            });
        }
        return micEnable;
    }

    public abstract void setUpRemoteVideo(final View remoteView);

    public abstract void setUpRemoteVideo(final View remoteView, int rotation);

    public void onPause() {
        startExitTimer(30);
    }

    public void onResume() {
        stopExitTimer();
    }

    public boolean getCurrentMicState(){
        return  micEnable;
    }
    /**
     * 获取房间号(ChannelId)
     */
    private void requestChannelId() {
        status = Status.CHANNEL_REQUEST;
        Log.d(TAG, "requestChannelId");
        avatarRobot.getChannelId(getChannelIdCallback);
    }

    protected abstract boolean setupChannel(String channelKey, String channelId);


    /**
     * 发送离开房间指令
     */
    public void sendLevelChannelMessage() {
        String message = PackActionMessageUtil.getInstance().packLeaveChannelControl().toString();
        sendMessage(message);
    }


    protected abstract void sendMessage(String message);

    public interface OnAvatarListener {

        public enum NetWorkStatus {
            INTERRUPTED, CONNECT_FAIL, CONNECT_SUCCESS
        }

        public static final int PERMISSION_ERROR = 100;
        public static final int START_AVATAR_ERROR = 500;
        public static final int START_AVATAR_ERROR_BESSY = 1;
        public static final int START_AVATAR_ERROR_BESSY_CALL = 2;

        public void onStarting();

        public void onAvatarStart(int width, int height);

        public void onAvatarStop();

        public void onError(int err, String msg);

        public void onAudioVolumeIndication(int totalVolume);

        public void onNetwork(NetWorkStatus status);

        public void onMicStateChange(boolean on);

    }


    public static class GetChannelIdCallback implements IAvatarRobot.GetChannelIdCallback {

        private SoftReference<IRtcManager> rtcManagerSF;

        public GetChannelIdCallback(IRtcManager rtcManager) {
            this.rtcManagerSF = new SoftReference(rtcManager);
        }

        @Override
        public void onSuccess(AvatarChannelInfo info) {
            Log.i(TAG, "requestChannelId=================onSuccess : " + info);
            IRtcManager rtcManager = rtcManagerSF.get();
            if (rtcManager != null) {
                rtcManager.status = Status.CHANNEL_REQUEST;
                if (rtcManager.channelId == null || !rtcManager.channelId.equals(info.getChannelId())) {
                    rtcManager.channelId = info.getChannelId();
                    rtcManager.channelKey = info.getChannelKey();

                    rtcManager.setupChannel(info.getChannelKey(), info.getChannelId());
                }
            }

        }

        @Override
        public void onError(int code, String msg) {
            Log.i(TAG, "requestChannelId=================onError : ");
            IRtcManager rtcManager = rtcManagerSF.get();
            if (rtcManager != null && rtcManager.listener != null) {
                rtcManager.doError(code, msg);
            }
        }

        public void clear() {
            rtcManagerSF.clear();
        }
    }


    public abstract SurfaceView createSurfaceView();

    public void startExitTimer(long second) {
        if (exitTimer == null) {
            exitTimer = new Timer();
            Log.d(TAG, "exitTimer start");
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onAvatarStop();
                        Log.d(TAG, "exitTimer exit");
                        exitTimer = null;
                    }
                }
            }, second * 1000);
        }
    }

    public void stopExitTimer() {
        if (exitTimer != null) {
            exitTimer.cancel();
            Log.d(TAG, "exitTimer stop");
            exitTimer = null;
        }
    }

    public interface InitResult {
        public void onSuccess();
    }


    protected void doError(int code) {
        status = START_FAIL;
        doStop();
        if (listener != null) {
            if(code == 0){
                code = START_AVATAR_ERROR;
            }
            listener.onError(code, "");
        }
    }

    protected void doError(int code, String msg) {
        status = START_FAIL;
        doStop();
        if (listener != null) {
            if(code == 0){
                code = START_AVATAR_ERROR;
            }
            listener.onError(code, msg);
        }
    }
}
