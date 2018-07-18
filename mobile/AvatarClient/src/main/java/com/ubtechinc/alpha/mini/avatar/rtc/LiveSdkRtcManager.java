package com.ubtechinc.alpha.mini.avatar.rtc;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveFunc;
import com.tencent.ilivesdk.ILiveMemStatusLisenter;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.adapter.CommonConstants;
import com.tencent.ilivesdk.adapter.ContextEngine;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.core.impl.ILVBLogin;
import com.tencent.ilivesdk.core.impl.ILVBRoom;
import com.tencent.ilivesdk.data.ILiveMessage;
import com.tencent.ilivesdk.data.msg.ILiveTextMessage;
import com.tencent.ilivesdk.view.AVRootView;
import com.ubtechinc.alpha.mini.avatar.PermissionManager;
import com.ubtechinc.alpha.mini.avatar.common.PermissionMicRequest;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.avatar.rtc.ilive.ConnectionManager;
import com.ubtechinc.alpha.mini.avatar.widget.ILiveSdkRootView;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager.OnAvatarListener.START_AVATAR_ERROR;

public class LiveSdkRtcManager extends IRtcManager {

    ILiveRoomManager iLiveRoomManager;

    private Timer micVolumeTimer;
    private Timer firstFrameTimer;
    private ConnectionManager connectionManager;

    private static int appId = 0;
    private static int accountType = 0;

    public LiveSdkRtcManager(Activity context, OnAvatarListener listener, IAvatarRobot avatarRobot) {
        super(context, listener, avatarRobot);
        setupRtcEngine(null);
    }

    @Override
    protected void setupRtcEngine(final InitResult initResult) {
        Log.d(TAG, "startContext start");
        if (ILiveSDK.getInstance().getAppId() != 0 ) {//APP ID 已经缓存了就直接使用缓存好的appId
            ILiveSDK.getInstance().initSdk(context, ILiveSDK.getInstance().getAppId(),
                    ILiveSDK.getInstance().getAccountType());
        }else{//否则从 IM中取，initSdk()方法会冲掉 TIMManager 里的appId
            ILiveSDK.getInstance().initSdk(context, Integer.valueOf(TIMManager.getInstance().getSdkAppId()),
                    Integer.valueOf(TIMManager.getInstance().getAccountType()));
        }
        iLiveRoomManager = ILiveRoomManager.getInstance();
        iLiveRoomManager.init(new ILiveRoomConfig());
        startContext(TIMManager.getInstance().getLoginUser(), new ILiveCallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.e(TAG, "startContext ini success");
                if (initResult != null) {
                    initResult.onSuccess();
                }
            }

            @Override
            public void onError(String s, int i, String s1) {
                Log.e(TAG, "startContext ini error" + s + s1);
                status = Status.INIT_ERROR;
                if (listener != null) {
                    listener.onError(START_AVATAR_ERROR, "");
                }
            }
        });
    }

    @Override
    protected void doStop() {
        stopMicVolumeTimer();
        stopCheckFirstFrameVideo();
        if (iLiveRoomManager != null && iLiveRoomManager.isEnterRoom()) {
            iLiveRoomManager.quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {

                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
        channelId = null;
    }

    @Override
    protected void doShutdown() {
        Log.d(TAG, "doShutdown");
        if (iLiveRoomManager != null) {
            Log.d(TAG, "isEnterRoom = " + iLiveRoomManager.isEnterRoom());
            if (iLiveRoomManager.isEnterRoom()) {
                iLiveRoomManager.quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.d(TAG, "quitRoom onSuccess ");
                        iLiveRoomManager.onDestory();
                        iLiveRoomManager.shutdown();

                        Log.d(TAG, "stopContext start ");
                        stopContext(new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "stopContext success ---------------");
                            }

                            @Override
                            public void onError(String s, int i, String s1) {

                                Log.d(TAG, "stopContext onError ---------------" + s + "|" + s1);
                            }
                        });
                        iLiveRoomManager = null;
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                        Log.d(TAG, String.format("quitRoom onError %s , %d , %s ", module, errCode, errMsg));
                    }
                });
            } else {
                iLiveRoomManager.onDestory();
                iLiveRoomManager.shutdown();
                Log.d(TAG, "stopContext start ");
                stopContext(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "stopContext success ---------------");
                    }

                    @Override
                    public void onError(String s, int i, String s1) {

                        Log.d(TAG, "stopContext onError ---------------" + s + "|" + s1);
                    }
                });
                iLiveRoomManager = null;
            }
            channelId = null;
        }

        stopMicVolumeTimer();
        stopCheckFirstFrameVideo();
        if (connectionManager != null) {
            connectionManager.destroy();
        }
    }

    @Override
    public void localStop() {
        doStop();
    }

    @Override
    public boolean micStatusSwitch() {
        if (micEnable) {
            enableMic(false);
            if (listener != null) {
                listener.onMicStateChange(false);
            }
        } else {
            PermissionManager.requestMicPermission(context, new PermissionMicRequest.PermissionMicCallback() {
                @Override
                public void onSuccessful() {
                    enableMic(true);
                    if (listener != null) {
                        listener.onMicStateChange(true);
                    }
                }

                @Override
                public void onFailure() {
                    if (listener != null) {
                        listener.onMicStateChange(false);
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

    @Override
    protected void enableMic(boolean enable) {
        if (iLiveRoomManager != null) {
            iLiveRoomManager.enableMic(enable);
            micEnable = enable;
            if (enable) {
                startMicVolumeTimer();
            } else {
                stopMicVolumeTimer();
            }
        }
    }

    @Override
    public void setUpRemoteVideo(View remoteView) {
        setUpRemoteVideo(remoteView, -1);
    }

    @Override
    public void setUpRemoteVideo(final View remoteView, final int rotation) {
        if (iLiveRoomManager != null) {
            Log.d(TAG, "setUpRemoteVideo " + remoteView + rotation);
            if (iLiveRoomManager.getRoomView() != null && iLiveRoomManager.getRoomView() != remoteView) {
                //iLiveRoomManager.getRoomView().getVideoGroup().onDestroy();
            }
            iLiveRoomManager.initAvRootView((AVRootView) remoteView);
            ((AVRootView) remoteView).setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
                @Override
                public void onSubViewCreated() {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (rotation != -1) {
                                ((AVRootView) remoteView).setDeviceRotation(rotation);
                            }
                            if (connectionManager != null) {
                                connectionManager.setVideoView((ILiveSdkRootView) remoteView);
                            }
                        }
                    });
                }
            });
            Log.d(TAG, "setUpRemoteVideo finish" + remoteView + rotation);
        }
    }

    @Override
    protected boolean setupChannel(final String channelKey, final String channelId) {
        Log.i(TAG, "setupChannel channelId = " + channelId);
        if (TextUtils.isEmpty(channelId)) {
            doError(500);
        } else {
            if (listener != null) {
                listener.onStarting();
            }
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doSetupChannel(channelKey, channelId);
                }
            });
        }
        return true;
    }

    private void doSetupChannel(String channelKey, String channelId) {
        Log.e(TAG, "doSetupChannel " + channelId);

        if (iLiveRoomManager != null) {
            Log.e(TAG, "isEnterRoom = " + iLiveRoomManager.isEnterRoom());
            if (!iLiveRoomManager.isEnterRoom()) {
                final ILiveRoomOption iLiveRoomOption = new ILiveRoomOption(channelId);
                iLiveRoomOption.autoRender(true)          // 开启自动渲染
                        .highAudioQuality(false) // 启用高清音质
                        .autoMic(false)
                        .autoCamera(false)
                        .controlRole("LiveGuest")
                        .roomDisconnectListener(disconnectListener)
                        .setRoomMemberStatusLisenter(iLiveMemStatusLisenter)
                        .groupType("Private");
                startCheckFirstFrameVideo();
                iLiveRoomManager.joinRoom(Integer.valueOf(channelId), iLiveRoomOption, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.e(TAG, "joinRoom onSuccess ");
//                        status = Status.FIRST_FRAME_OBTAINED;
//                        stopCheckFirstFrameVideo();
                        if (listener != null) {
                            //TODO listener.onAvatarStart(0, 0);
                        }
                        if (iLiveRoomManager != null) {
                            if (iLiveRoomManager.getRoomView() != videoView) {
                                setUpRemoteVideo(videoView, rotation);
                            }
                            if (connectionManager == null) {
                                connectionManager = ConnectionManager.getInstance();
                            }
                            connectionManager.setConnectionListener(connectionListener);
                            connectionManager.start((ILiveSdkRootView) videoView);
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Log.e(TAG, "joinRoom onError " + module + errMsg);
                        doError(500);
                    }
                });
            }
        }
    }

    @Override
    protected void sendMessage(final String message) {
        if (iLiveRoomManager == null) {
            return;
        }
        ILiveMessage iLiveMessage = new ILiveTextMessage(message);
        String robotUserId= avatarRobot.avatarImUserId();
        Log.d(TAG,"robotUserId = " +robotUserId);
        iLiveRoomManager.sendC2CMessage(robotUserId, iLiveMessage, new ILiveCallBack<TIMMessage>() {
            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.d(TAG, "sendSuccess = " + message);
            }

            @Override
            public void onError(String s, int i, String s1) {
                Log.d(TAG, "sendError = " + message);
                Log.d(TAG, "sendError = " +i +  s1);
            }
        });
    }


    @Override
    public SurfaceView createSurfaceView() {
        AVRootView avRootView = new ILiveSdkRootView(context);
        avRootView.setAutoOrientation(false);
        Log.d(TAG, "createSurfaceView");
        return avRootView;
    }

    private void startMicVolumeTimer() {
        if (micVolumeTimer == null) {
            micVolumeTimer = new Timer();
            micVolumeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ILiveSDK.getInstance().getAvAudioCtrl() != null){
                                int getDynamicVolume = ILiveSDK.getInstance().getAvAudioCtrl().getDynamicVolume();
                                if (listener != null) {
                                    listener.onAudioVolumeIndication(getDynamicVolume  / 10);
                                }
                            }
                        }
                    });
                }
            }, 0, 300);
        }
    }

    private void stopMicVolumeTimer() {
        if (micVolumeTimer != null) {
            micVolumeTimer.cancel();
            micVolumeTimer = null;
        }
    }

    /**
     * 8秒后如果还没有获取到第一帧视频画面，就重新显示开启阿凡达模式按钮
     */
    private void startCheckFirstFrameVideo() {
        if (firstFrameTimer != null) {
            firstFrameTimer.cancel();
        }
        firstFrameTimer = new Timer();
        firstFrameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                doError(500);
            }
        }, 8000);
    }

    private void stopCheckFirstFrameVideo() {
        if (firstFrameTimer != null) {
            firstFrameTimer.cancel();
            firstFrameTimer = null;
        }
    }

    private ILiveRoomOption.onRoomDisconnectListener disconnectListener = new ILiveRoomOption.onRoomDisconnectListener() {

        @Override
        public void onRoomDisconnect(int i, String s) {
            Log.d(TAG, "disconnect = " + i + "===" + s);
        }
    };

    private ILiveMemStatusLisenter iLiveMemStatusLisenter = new ILiveMemStatusLisenter() {
        @Override
        public boolean onEndpointsUpdateInfo(int i, String[] strings) {
            Log.d(TAG, "onEndpointsUpdateInfo  " + i + "strings = " + strings);
            return false;
        }
    };

    @Override
    public void onPause() {
        if (iLiveRoomManager != null) {
            iLiveRoomManager.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (iLiveRoomManager != null) {
            iLiveRoomManager.onResume();
        }
        super.onResume();
    }

    private void startContext(String identifier, final ILiveCallBack callBack) {
        try {
            Field mMyUserId = ILVBLogin.class.getDeclaredField("mMyUserId");
            mMyUserId.setAccessible(true);
            mMyUserId.set(ILiveLoginManager.getInstance(), TIMManager.getInstance().getLoginUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContextEngine contextAdapter = ILiveSDK.getInstance().getContextEngine();
        ILiveLog.di(TAG, "startContext");
        CommonConstants.ILiveUserInfo info = new CommonConstants.ILiveUserInfo();
        info.sdkAppId = ILiveSDK.getInstance().getAppId();
        info.accountType = ILiveSDK.getInstance().getAccountType();
        info.identifier = identifier;
        contextAdapter.setUserInfo(info);
        contextAdapter.start(ILiveLog.getLogLevel(), new ILiveCallBack() {
            public void onSuccess(Object data) {
                ILiveFunc.notifySuccess(callBack, Integer.valueOf(0));
                ((ILVBRoom) ILiveRoomManager.getInstance()).afterLogin();
                try {
                    Field bLogin = ILVBLogin.class.getDeclaredField("bLogin");
                    bLogin.setAccessible(true);
                    bLogin.set(ILiveLoginManager.getInstance(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onError(String module, int errCode, String errMsg) {
                ILiveFunc.notifyError(callBack, module, errCode, errMsg);
                stopContext((ILiveCallBack) null);
            }
        });
    }

    void stopContext(final ILiveCallBack logoutListener) {
        ILiveLog.di(TAG, "stopContext");
        ILiveSDK.getInstance().getContextEngine().stop(new ILiveCallBack() {
            public void onSuccess(Object data) {
                ILiveFunc.notifySuccess(logoutListener, Integer.valueOf(0));
            }

            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.ke(TAG, "stopContext", module, errCode, errMsg);
                ILiveFunc.notifyError(logoutListener, module, errCode, errMsg);
            }
        });
    }

    private ConnectionManager.ConnectionListener connectionListener = new ConnectionManager.ConnectionListener() {
        @Override
        public void onConnectionInterrupted() {
            if (listener != null) {
                listener.onNetwork(OnAvatarListener.NetWorkStatus.INTERRUPTED);
            }
        }

        @Override
        public void onConnectionLost() {
            if (listener != null) {
                status = Status.START_FAIL;
                doStop();
                listener.onNetwork(OnAvatarListener.NetWorkStatus.CONNECT_FAIL);
            }
        }

        @Override
        public void onReconnectSuccess() {
            if (listener != null) {
                listener.onNetwork(OnAvatarListener.NetWorkStatus.CONNECT_SUCCESS);
            }
        }

        @Override
        public void onFirstFrameReceive() {
            status = Status.FIRST_FRAME_OBTAINED;
            stopCheckFirstFrameVideo();
            if (listener != null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAvatarStart(0, 0);
                    }
                });
            }
        }
    };

}
