package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.AdbSwitch;
import com.ubtechinc.alpha.DetectUpgradeProto;
import com.ubtechinc.alpha.GetMultiConvState;
import com.ubtechinc.alpha.GetRobotConfiguration;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMiniSettingBinding;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.RobotUnbindLive;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;
import com.ubtechinc.alpha.mini.viewmodel.UpgradeViewModel;
import com.ubtechinc.alpha.mini.viewmodel.unbind.MasterUnbindDialog;
import com.ubtechinc.alpha.mini.viewmodel.unbind.RobotUnbindBussiness;
import com.ubtechinc.alpha.mini.viewmodel.unbind.SimpleUnbindDialog;
import com.ubtechinc.alpha.mini.viewmodel.unbind.UnbindDialog;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.UserInfo;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import static com.ubtechinc.alpha.im.IMCmdId.IM_GET_MULTI_CONVERSATION_STATE_REQUEST;
import static com.ubtechinc.alpha.im.IMCmdId.IM_GET_ROBOT_CONFIG_REQUEST;
import static com.ubtechinc.alpha.im.IMCmdId.IM_VERSION;

/**
 * @Date: 2017/11/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人设置页面(机器人档案，机器人Wifi设置，固件信息，系统信息，解绑入口)
 */

public class AlphaMiniSettingActivity extends BaseToolbarActivity implements View.OnClickListener {

    private ActivityMiniSettingBinding mDataBinding;
    private UserInfo mUserInfo;
    private RobotInfo mRobotInfo;
    private String mRobotUserId;
    private GetRobotConfiguration.GetRobotConfigurationResponse mResponse;

    private boolean dataOpen;

    private UpgradeViewModel upgradeViewModel;

    private int adbOpenCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_mini_setting);
        mRobotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        upgradeViewModel = new UpgradeViewModel();
        init();

    }


    private void init() {
        View toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.robot_setting), View.GONE, false);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        mDataBinding.meAvatarBg.setOnClickListener(this);
        mDataBinding.btnUnbind.setOnClickListener(this);
        mDataBinding.settingRobotWifiRl.setOnClickListener(this);
        mDataBinding.systemInfoRl.setOnClickListener(this);
        mDataBinding.settingCellularDataRl.setOnClickListener(this);
        mDataBinding.headwareRl.setOnClickListener(this);
        mDataBinding.voiceRl.setOnClickListener(this);
        mRobotUserId = MyRobotsLive.getInstance().getRobotUserId();
        mDataBinding.setSerailNumber(String.format(getResources().getString(R.string.serial_number_1), mRobotUserId));
        mUserInfo = AuthLive.getInstance().getCurrentUser();
        if (mUserInfo != null) {
            mDataBinding.setUserName(mUserInfo.getNickName());
            if (mRobotInfo != null && mRobotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
                Glide.with(this).load(R.drawable.img_robot_avatar_online).into(mDataBinding.meAvatarBg);
            } else {
                Glide.with(this).load(R.drawable.img_robot_avatar_offline).into(mDataBinding.meAvatarBg);
            }
            if (mRobotInfo != null) {
                if (mRobotInfo.isMaster()) {
                    mDataBinding.settingCellularDataRl.setVisibility(View.VISIBLE);
                    mDataBinding.headwareRl.setVisibility(View.VISIBLE);
                } else {
                    mDataBinding.settingCellularDataRl.setVisibility(View.GONE);
                    mDataBinding.headwareRl.setVisibility(View.GONE);
                }
            }
        }
        mDataBinding.setWifiName(getResources().getString(R.string.getting_robot_wifi));

    }

    private void checkAdb() {
        if (SharedPreferencesUtils.getBoolean(getApplicationContext(), "ADB-SWITCH"+mRobotUserId, false)) {
            mDataBinding.adbSwitchRl.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.adbSwitchRl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void observerCurrentRobot(@Nullable RobotInfo robotInfo) {
        super.observerCurrentRobot(robotInfo);
        if (robotInfo != null) {
            if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
                Glide.with(this).load(R.drawable.img_robot_avatar_online).into(mDataBinding.meAvatarBg);
            } else {
                Glide.with(this).load(R.drawable.img_robot_avatar_offline).into(mDataBinding.meAvatarBg);
            }
        }
        if (robotInfo != null) {
            if (robotInfo.isMaster()) {
                mDataBinding.settingCellularDataRl.setVisibility(View.VISIBLE);
            } else {
                mDataBinding.settingCellularDataRl.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAdb();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IM_GET_ROBOT_CONFIG_REQUEST, IM_VERSION, null, mRobotUserId, mICallback);
        checkUpgrade();

    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        mDataBinding.setNetwork(networkConnectState);
        checkUpgrade();
        if (isColsedConnectLayout()) {
            return;
        }

        if (networkConnectState) {
            if (mResponse != null) {
                String wifiName = mResponse.getWifiname();
                if ("<unknown ssid>".equals(wifiName)){
                    mDataBinding.setWifiName("未连接");
                }else{
                    mDataBinding.setWifiName(wifiName.replace("\"", ""));
                }
            } else {
                mDataBinding.setWifiName("");
            }
        } else {
            mDataBinding.setWifiName("");
        }

        if (!networkConnectState) {
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.mobile_connected_error);
            return;
        }

        if (robotConnectState) {
            if (mResponse != null) {
                String wifiName = mResponse.getWifiname();
                if ("<unknown ssid>".equals(wifiName)){
                    mDataBinding.setWifiName("未连接");
                }else{
                    mDataBinding.setWifiName(wifiName.replace("\"", ""));
                }
            } else {
                mDataBinding.setWifiName(getResources().getString(R.string.getting_robot_wifi));
            }
        } else {
            mDataBinding.setWifiName("未连接");
        }

        if (!robotConnectState) {
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.robot_connected_error);
            return;
        }
        connectFailed.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_unbind:
                unbindMyself();
                break;
            case R.id.setting_robot_wifi_rl:
                PageRouter.toSettingShowRobotWifiActivity(this, mDataBinding.getWifiName());
                break;
            case R.id.system_info_rl:
                if (mResponse != null) {
                    PageRouter.toSystemInfoActivity(AlphaMiniSettingActivity.this, mResponse);
                } else {
                    toast("没有获取到系统信息");
                }
                break;
            case R.id.setting_cellular_data_rl:
                if (mRobotInfo != null) {
                    PageRouter.toCellDataSetting(AlphaMiniSettingActivity.this, dataOpen, false);
                }
                break;
            case R.id.headware_rl:
                if (mRobotInfo != null) {
                    PageRouter.toUpgrade(AlphaMiniSettingActivity.this);
                }
                break;
            case R.id.voice_rl:
                if (mRobotInfo != null) {
                    PageRouter.toMutilVoiceInteract(AlphaMiniSettingActivity.this);
                }
                break;
            case R.id.me_avatar_bg:
                break;
            case R.id.user_name_tv:
                break;
        }
    }


    private boolean isDoUnbind;

    private void unbindMyself() {
        if (isDoUnbind) {
            return;
        }
        isDoUnbind = true;
        final RobotUnbindBussiness bussiness = new RobotUnbindBussiness();
        final String userId = MyRobotsLive.getInstance().getRobotUserId();
        bussiness.setUnbindWaysListener(new RobotUnbindBussiness.IOnUnbinbWaysListener() {
            @Override
            public void onGetUnbindWays(RobotUnbindLive unbindLive) {
                isDoUnbind = false;
                switch (unbindLive.getUnbindCategory()) {
                    case MASTER:
                        createMasterUnbindDialog(unbindLive, bussiness);
                        break;
                    case SIMPLE:
                        createSimpleUnbindDialog(bussiness, userId);
                        break;
                    case NO_NEED:
                        ToastUtils.showShortToast("当前机器人未绑定，不需要解绑");
                        break;
                }
            }
        });
        bussiness.getRobotUnbindWays(this, userId);
    }

    SimpleUnbindDialog simpleUnbindDialog; // TODO: 2018/1/8 可能有问题，如果此时账号发生变化


    private void createSimpleUnbindDialog(RobotUnbindBussiness unbindBussiness, String mRobotUserId) {
        if (simpleUnbindDialog == null) {
            simpleUnbindDialog = new SimpleUnbindDialog(AlphaMiniSettingActivity.this);
            simpleUnbindDialog.setSearialNumber(mRobotUserId);
            simpleUnbindDialog.addOnUnbindListener(new UnbindDialog.IOnUnbindListener() {

                @Override
                public void onUnbindSuccess() {
                    finish();
                }

                @Override
                public void onUnbindFailed() {

                }
            });
        }
        unbindBussiness.showUnbindDialog(simpleUnbindDialog);
    }

    MasterUnbindDialog dialog;

    private void createMasterUnbindDialog(RobotUnbindLive unbindLive, RobotUnbindBussiness unbindBussiness) {
        if (dialog == null) {
            dialog = new MasterUnbindDialog(AlphaMiniSettingActivity.this, unbindLive.getRobotOwners());
            dialog.addOnUnbindListener(new UnbindDialog.IOnUnbindListener() {
                @Override
                public void onUnbindSuccess() {
                    finish();
                }

                @Override
                public void onUnbindFailed() {

                }
            });
        }
        unbindBussiness.showUnbindDialog(dialog);
    }

    ICallback<GetRobotConfiguration.GetRobotConfigurationResponse> mICallback = new ICallback<GetRobotConfiguration.GetRobotConfigurationResponse>() {
        @Override
        public void onSuccess(final GetRobotConfiguration.GetRobotConfigurationResponse data) {
            LogUtils.i("wll", "wifiname========" + data.getWifiname());
            mResponse = data;
            String wifiName = mResponse.getWifiname();
            if ("<unknown ssid>".equals(wifiName)){
                mDataBinding.setWifiName("未连接");
            }else{
                mDataBinding.setWifiName(wifiName.replace("\"", ""));
            }
            if (data.getIsSimExist()) {
                dataOpen = data.getIsOpenData();
                if (data.getIsOpenData()) {
                    mDataBinding.setCellularState(1);
                } else {
                    mDataBinding.setCellularState(2);
                }
            } else {
                mDataBinding.setCellularState(0);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDataBinding.switchBtnUsb.setOnCheckedChangeListener(null);
                    mDataBinding.switchBtnUsb.setChecked(data.getIsAdbEnable());
                    mDataBinding.switchBtnUsb.setOnCheckedChangeListener(mSwitcherChangedListener);
                }
            });

        }

        @Override
        public void onError(ThrowableWrapper e) {

        }
    };

    private void checkUpgrade() {
        if (getRobotConnect() && getNetworkConnectState()) {
            upgradeViewModel.checkUpgrade().observe(this, new Observer<LiveResult>() {
                @Override
                public void onChanged(@Nullable LiveResult liveResult) {
                    switch (liveResult.getState()) {
                        case SUCCESS:
                            liveResult.removeObserver(this);
                            DetectUpgradeProto.DetectUpgrade detectUpgrade = (DetectUpgradeProto.DetectUpgrade) liveResult.getData();
                            handleWithUpgrade(detectUpgrade);
                            break;
                        case FAIL:
                            liveResult.removeObserver(this);
                            showHasUpgrade(false);
                            break;
                        case LOADING:
                            break;
                    }
                }
            });
        } else {
            showHasUpgrade(false);
        }
    }

    private void handleWithUpgrade(DetectUpgradeProto.DetectUpgrade detectUpgrade) {
        if (detectUpgrade != null) {
            switch (detectUpgrade.getState()) {
                case NO_UPDATE:
                    showHasUpgrade(false);
                    break;
                default:
                    showHasUpgrade(true);
                    break;
            }
        } else {
            showHasUpgrade(false);
        }

    }

    private void showHasUpgrade(boolean show) {
        if (show) {
            mDataBinding.upgradeFlag.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.upgradeFlag.setVisibility(View.GONE);
        }
    }

    /******************************************** 以下 ADB 开关 ************************************************/

    private CompoundButton.OnCheckedChangeListener mSwitcherChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //TODO im cmd 2 robot
            switchAdb(isChecked);
        }
    };


    private void switchAdb(final boolean opened) {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (currentRobot != null && currentRobot.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            AdbSwitch.AdbSwitchRequest.Builder builder = AdbSwitch.AdbSwitchRequest.newBuilder();
            builder.setOpen(opened);
            Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_ADB_SWITCH_REQUEST, IMCmdId.IM_VERSION, builder.build(), currentRobot.getRobotUserId(), new ICallback<AdbSwitch.AdbSwitchResponse>() {

                @Override
                public void onSuccess(AdbSwitch.AdbSwitchResponse data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AlphaMiniApplication.getInstance(), "设置成功!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError(ThrowableWrapper e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AlphaMiniApplication.getInstance(), "设置失败!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
    }


}
