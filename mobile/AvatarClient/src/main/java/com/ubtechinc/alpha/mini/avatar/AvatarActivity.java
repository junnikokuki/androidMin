package com.ubtechinc.alpha.mini.avatar;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.mini.avatar.common.PreferenceConstants;
import com.ubtechinc.alpha.mini.avatar.common.PreferencesManager;
import com.ubtechinc.alpha.mini.avatar.db.AvatarActionHelper;
import com.ubtechinc.alpha.mini.avatar.robot.AvatarRobotProxy;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager;
import com.ubtechinc.alpha.mini.avatar.rtc.LiveSdkRtcManager;
import com.ubtechinc.alpha.mini.avatar.viewutils.AvatarUserListManger;
import com.ubtechinc.alpha.mini.avatar.viewutils.GuideViewUtil;
import com.ubtechinc.alpha.mini.avatar.viewutils.VideoPanelUtil;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;
import com.ubtechinc.alpha.mini.avatar.widget.MaterialDialog;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AvatarActivity extends FragmentActivity implements VideoPanelUtil.VideoPanelListener {

    private static final String TAG = AvatarActivity.class.getSimpleName();

    private AvatarBaseFragment fragmentH;
    private AvatarBaseFragment fragment;
    private AvatarBaseFragment currentFragment;


    private IRtcManager rtcManager;
    private IAvatarRobot avatarRobot = new AvatarRobotProxy(AvatarInject.getInstance().getAvatarRobot());

    public  List<AvatarActionModel> favActionList = new ArrayList<>();

    private boolean onBackground = false;
    private boolean powerShowClosed = false;
    private boolean exitable = false;


    private AvatarStateManager manager;

    protected AvatarUserListManger userListManger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        rtcManager = new LiveSdkRtcManager(this, new AvatarListener(), avatarRobot);
        manager = new AvatarStateManager();
        userListManger = AvatarUserListManger.get();
        favActionList = AvatarActionHelper.getInstance().getFavActionModeList(this, avatarRobot.userId());
        initView();
        avatarRobot.onRobotEvent(onRobotEventListener);
    }

    public void initView() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            toPortrait();
        } else {
            toLandscape();
        }
        manager.setState(AvatarStateManager.State.CONNECTING);
        exitable(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!onBackground) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                toLandscape();
            } else {
                toPortrait();
            }
        }
    }

    private void toLandscape() {
        if (fragmentH == null) {
            fragmentH = new AvatarFragmentH();
            fragmentH.init(rtcManager);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragmentH);
        currentFragment = fragmentH;
        ft.commit();

        manager.setStateListener((AvatarStateManager.IAvatarStateListener) fragmentH);

        ImmersionBar.with(this).transparentStatusBar()
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .statusBarDarkFont(false)
                .fitsSystemWindows(false).init();
    }

    private void toPortrait() {
        if (fragment == null) {
            fragment = new AvatarFragmentV();
            fragment.init(rtcManager);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        currentFragment = fragment;
        ft.commit();

        manager.setStateListener((AvatarStateManager.IAvatarStateListener) fragment);

        ImmersionBar.with(this).transparentStatusBar()
                .hideBar(BarHide.FLAG_SHOW_BAR)
                .statusBarDarkFont(false)
                .fitsSystemWindows(false).init();
    }



    public void doRetry() {
        currentFragment.restart();
        manager.setState(AvatarStateManager.State.CONNECTING);
        exitable(false);
    }


    @Override
    public void onScreenshot() {
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(AvatarControl.CommandType.TAKE_PIC);
        avatarRobot.sendControlMessage(builder);
    }


    private class AvatarListener implements IRtcManager.OnAvatarListener {

        @Override
        public void onStarting() {

        }

        @Override
        public void onAvatarStart(int width, int height) {
            showGuideView();
            heartTimer = new TimerTask() {
                @Override
                public void run() {
                    sendHeartMessage();
                }
            };
            timer.schedule(heartTimer, 0, 5000);
            manager.setState(AvatarStateManager.State.NORMAL);
            powerShowClosed = false;
            exitable(true);
        }

        @Override
        public void onAvatarStop() {
            finish();
        }

        @Override
        public void onError(final int err, final String msg) {
            switch (err) {
                case START_AVATAR_ERROR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            manager.setState(AvatarStateManager.State.START_FAILED);
                            exitable(true);
                        }
                    });
                    break;
                case START_AVATAR_ERROR_BESSY:
                case START_AVATAR_ERROR_BESSY_CALL:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exitable(true);
                            showBusyDialog(msg);
                        }
                    });
                    break;
            }
        }

        @Override
        public void onAudioVolumeIndication(final int totalVolume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentFragment.onAudioVolumeIndication(totalVolume);
                }
            });
        }

        @Override
        public void onNetwork(NetWorkStatus status) {
            switch (status) {
                case INTERRUPTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            manager.setState(AvatarStateManager.State.CONNECTING);
                        }
                    });
                    break;
                case CONNECT_FAIL:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                            // 获取当前的网络连接是否可用
                            boolean available = false;
                            if (networkInfo != null) {
                                available = networkInfo.isAvailable();
                            }
                            if (available) {
                                manager.setState(AvatarStateManager.State.CONNECTFAILED);
                            } else {
                                manager.setState(AvatarStateManager.State.NETWORK_ERROR);
                            }
                            rtcManager.localStop();
                            exitable(true);
                        }
                    });
                    break;
                case CONNECT_SUCCESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            manager.setState(AvatarStateManager.State.NORMAL);
                            exitable(true);
                        }
                    });

                    break;
                default:
                    break;
            }
        }

        @Override
        public void onMicStateChange(boolean on) {
            currentFragment.changeMicState(on);
        }
    }

    protected void onPowerChange(final int powerValue, final boolean isLowPower, final boolean isCharging) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "powerValue==" + powerValue);
                Log.d(TAG, "isLowPower==" + isLowPower);
                if (isLowPower) {
                    Toast.makeText(AvatarActivity.this, R.string.low_power, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (!powerShowClosed) {

                    }
                }
            }
        });
    }

    public boolean getCurrentMicState(){
        return rtcManager.getCurrentMicState();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "执行onDestroy");
        avatarRobot.clearRobotEventListener(onRobotEventListener);
        ImmersionBar.with(this).destroy();
        userListManger.release();
        timer.purge();
        timer.cancel();
        rtcManager.shutdown();
    }


    public void exit(boolean needDialog) {
        if (!exitable) {
            return;
        }
        if (needDialog) {
            final MaterialDialog exitDialog = new MaterialDialog(AvatarActivity.this);
            exitDialog.setMessage("是否退出视频监控模式");
            exitDialog.setPositiveButton("是", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitDialog.dismiss();
                    sendExitMessage();
                    finish();
                }
            });

            exitDialog.setNegativeButton("否", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitDialog.dismiss();
                }
            }).setEnableKeyCode(false);
            exitDialog.show();
        } else {
            sendExitMessage();
            finish();
        }
    }

    private void sendExitMessage(){
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(AvatarControl.CommandType.EXIT);
        avatarRobot.sendControlMessage(builder);
    }

    public void sendControlMessage(AvatarControl.ControlRequest.Builder builder){
        avatarRobot.sendControlMessage(builder);
    }

    public void sendActionMessage(AvatarControl.ControlRequest.Builder builder){
        avatarRobot.sendControlMessage(builder, new IAvatarRobot.IMsgCallBack() {
            @Override
            public void onSucces(AlphaMessageOuterClass.AlphaMessage alphaMessage) {
                byte[] byts = alphaMessage.getBodyData().toByteArray();
                AvatarControl.ControlResponse response = (AvatarControl.ControlResponse) ProtoBufferDispose.unPackData(AvatarControl.ControlResponse.class, byts);
                AvatarControl.Result result = response.getResult();
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i(TAG, " send Action Message onError: " );

            }
        });
    }

    private void handleMessageReponse(AvatarControl.Result result) {
        final int titleRes = R.string.robot_dialog_title;
        final int messageRes;
        if (result.getNumber() == AvatarControl.Result.FORBIDDEN_VALUE){
            messageRes = R.string.robot_control_message_forbid;
        }else if (result.getNumber() == AvatarControl.Result.NO_ALLOW_VALUE){
            messageRes = R.string.robot_control_message_standing;
        }else if (result.getNumber() == AvatarControl.Result.DO_NOTHING_VALUE){
            messageRes = R.string.robot_control_message_succ;
        }else if (result.getNumber() == AvatarControl.Result.UNKNOWN_ERROR_VALUE){
            messageRes = R.string.robot_control_message_fail;
        }else{
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MaterialDialog dialog = new MaterialDialog(AvatarActivity.this);
                dialog.setTitle(titleRes)
                        .setMessage(messageRes)
                        .setPositiveButton(R.string.str_get_it, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }


    public void sendHeartMessage(){
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(AvatarControl.CommandType.HEARTBEAT);
        avatarRobot.sendControlMessage(builder);
    }

    public void robotStand(){
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(AvatarControl.CommandType.STAN_UP);
        avatarRobot.sendControlMessage(builder, new IAvatarRobot.IMsgCallBack() {
            @Override
            public void onSucces(AlphaMessageOuterClass.AlphaMessage alphaMessage) {
                byte[] byts = alphaMessage.getBodyData().toByteArray();
                AvatarControl.ControlResponse response = (AvatarControl.ControlResponse) ProtoBufferDispose.unPackData(AvatarControl.ControlResponse.class, byts);
                AvatarControl.Result result = response.getResult();
                handleMessageReponse(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment != null){
                            fragment.onStandCommandReponse();
                        }
                        if (fragmentH != null){
                            fragmentH.onStandCommandReponse();
                        }
                    }
                });

            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i(TAG, " send robotStand Message onError: " );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment != null){
                            fragment.onStandCommandReponse();
                        }
                        if (fragmentH != null){
                            fragmentH.onStandCommandReponse();
                        }
                    }
                });

            }
        });
    }


    private IAvatarRobot.OnRobotEventListener onRobotEventListener = new IAvatarRobot.OnRobotEventListener() {
        @Override
        public void onDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "im disconnect");
                }
            });
        }

        @Override
        public void onAvatarStopped(final int reasonCode, final String reason) {
            final int titleResId = R.string.robot_exit_title  ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MaterialDialog dialog = new MaterialDialog(AvatarActivity.this);
                    dialog.setTitle(titleResId)
                            .setMessage(reason)
                            .setPositiveButton(R.string.str_get_it, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                }
            });
        }

        @Override
        public void onRobotFaildown() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MaterialDialog dialog = new MaterialDialog(AvatarActivity.this);
                    dialog.setTitle(R.string.robot_exit_title).setMessage(R.string.robot_fail_down)
                            .setPositiveButton(R.string.str_get_it, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });

        }

        @Override
        public void onUserListsChange(final List<String> allUsers, final List<String> enterUsers, final List<String> leaveUsers) {
            Log.i("0522", "onUserListsChange: " + allUsers.size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userListManger.avatarUserChange(allUsers, enterUsers, leaveUsers);
                }
            });
        }

        @Override
        public void onLowPowerTips() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final MaterialDialog dialog = new MaterialDialog(AvatarActivity.this);
                    dialog.setTitle(R.string.robot_dialog_title).setMessage(R.string.robot_low_power_30_message)
                            .setPositiveButton(R.string.str_get_it, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });


        }
    };

    @Override
    protected void onPause() {
        if (rtcManager != null) {
            rtcManager.onPause();
        }
        onBackground = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (onBackground && rtcManager != null) {
            rtcManager.onResume();
        }
        onBackground = false;
        super.onResume();
    }


    public void showGuideView() {
        if (PreferencesManager.getInstance(AvatarActivity.this.getApplicationContext()).get(PreferenceConstants.IS_FIRST_START_AVATAR, true)) {
            GuideViewUtil guideViewUtil = new GuideViewUtil(AvatarActivity.this);
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            guideViewUtil.showAvatarGudieView1(currentFragment.getWalkControl(), currentFragment.getHeaderControl(), new GuideViewUtil.GuideViewFinishListener() {
                @Override
                public void onFinish() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    PreferencesManager.getInstance(AvatarActivity.this).put(PreferenceConstants.IS_FIRST_START_AVATAR, false);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exitable(boolean exitable) {
        this.exitable = exitable;
    }

    public void showBusyDialog(String msg){
        //子类实现
    }

    public void showTopMessage(String msg){
        //子类实现
    }



    public AvatarStateManager getManager() {
        return manager;
    }

    public AvatarUserListManger getUserListManger() {
        return userListManger;
    }

    private Timer timer = new Timer();

    private TimerTask heartTimer;

}
