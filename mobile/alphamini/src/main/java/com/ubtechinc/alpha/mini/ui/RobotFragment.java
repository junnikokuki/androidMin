package com.ubtechinc.alpha.mini.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tcl.joylockscreen.view.blur.BlurUtil;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtech.utilcode.utils.notification.Subscriber;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.databinding.FragmentRobotBinding;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.SimState;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.PowerLive;
import com.ubtechinc.alpha.mini.event.RobotImResponseEvent;
import com.ubtechinc.alpha.mini.qqmusic.PresentView;
import com.ubtechinc.alpha.mini.qqmusic.QQMusicStatsImp;
import com.ubtechinc.alpha.mini.qqmusic.model.QQMusicModel;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.animation.RobotFragmentAnimator;
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity;
import com.ubtechinc.alpha.mini.ui.utils.RobotReconnectTipUtil;
import com.ubtechinc.alpha.mini.ui.utils.RobotStateViewUtil;
import com.ubtechinc.alpha.mini.utils.PermissionUtils;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.HomeMorePopupWindow;
import com.ubtechinc.alpha.mini.widget.HomePageSkillView;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.RobotPopupWindow;
import com.ubtechinc.alpha.mini.widget.RobotPresentWindow;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_AVATAR;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_CALL;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_PHOTO;
import static com.ubtechinc.alpha.mini.utils.PermissionUtils.checkPermission;

public class RobotFragment extends BaseFragment implements RobotPopupWindow.SwitchRobotListener, RobotFragmentAnimator.IRobotFragAnimationListner
        , RobotFragmentAnimator.IRobotSwitchAnimationListener, HomePageSkillView.OnBackgroundListener {

    public static final String TAG = "RobotFragment";

    public static final String SWITCH_TIPS = "switch_tips";

    public static final int ROBOT_WITH_BORAD_KEY = 0;

    public static final int ROBOT_WITHOUT_BORAD_KEY = 1;

    public static final int SWITCHING_TIME = 1000;
    private static final int PRESENT_SHOW_DELAY = 1200;
    private static final int PRESENT_HIDE_DELAY = 100;

    private List<RobotInfo> robotInfos = Collections.EMPTY_LIST;

    private RobotInfo currentRobot;

    private NetworkHelper.NetworkInductor networkInductor;

    private RobotFragmentAnimator robotFragmentAnimator;

    private Handler mHandler;
    private boolean switchTipsShowed;

    private MaterialDialog contactPrivacyPolicyDialog;

    RobotsViewModel robotsViewModel;

    FragmentRobotBinding binding;

    RobotStateViewUtil robotStateViewUtil; //处理连接提示框的逻辑工具类

    RobotPopupWindow robotPopupWindow;
    RobotPresentWindow robotPresentWindow;

    HomeMorePopupWindow homeMorePopupWindow;
    private boolean startScanBle;
    private boolean mJumpBanding;
    private PresentView presentView;
    private boolean isResume;

    private Map<Integer, Bitmap> mBlurBitmapMap = new HashMap<>();

    private RobotReconnectTipUtil robotReconnectTipUtil;
    private boolean toUser;
    private boolean isLoadSuc;
    private boolean isSelect = true;

    private Observer<QQMusicStatsImp> qqMusicStatsImpObserver = new Observer<QQMusicStatsImp>() {
        @Override
        public void onChanged(@Nullable QQMusicStatsImp qqMusicStatsImp) {
            if(isResume) {
                presentView();
                if(qqMusicStatsImp.hasPresent()) {
                    presentView.toShow();
                } else {
                    presentView.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_robot, null, false);
        mHandler = new Handler();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setUpAnimation();
        if (robotsViewModel == null) {
            robotsViewModel = RobotsViewModel.get();
        }
        switchTipsShowed = SPUtils.get().getBoolean(SWITCH_TIPS + AuthLive.getInstance().getUserId());
        PowerLive.get().observe(this, new Observer<PowerLive>() {
            @Override
            public void onChanged(@Nullable PowerLive powerLive) {
                if (binding != null) {
                    binding.setPowerValue(powerLive.getCurrentValue());
                }
            }
        });
        QQMusicStatsImp.getInstance().observeForever(qqMusicStatsImpObserver);
        presentView = binding.present;
        presentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQMusicModel.getInstance(getActivity()).updateMemberStatus();
                showRobotPresentPopupWindow(v);
            }
        });
    }


    private void setUpAnimation() {
        robotFragmentAnimator = new RobotFragmentAnimator(binding);
        robotFragmentAnimator.setAnimationListener(this);
        robotFragmentAnimator.setSwitchAnimationListener(this);
        robotFragmentAnimator.startRobotAnimation();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if(toUser) {
            toUser = false;
            QQMusicModel.getInstance(getActivity()).updateDevice(MyRobotsLive.getInstance().getRobotUserId());
        }
        presentView();
        isResume = true;
        Log.d(TAG, " list : " + (MyRobotsLive.getInstance().getRobots().getData() != null));
        startScan();
    }

    private void presentView() {
        if(QQMusicStatsImp.getInstance().hasPresent()) {
            presentView.toShow();
        }else{
            presentView.setVisibility(View.INVISIBLE);
        }
    }

    private void initData() {
        Log.d(TAG, "initData");
        MyRobotsLive.getInstance().getRobots().observeForever(robotsObserver);
        MyRobotsLive.getInstance().getCurrentRobot().observeForever(currentRobotObserver);
        MyRobotsLive.getInstance().getSelectToRobot().observeForever(selectToRobotObserver);
        networkInductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                LogUtils.d(TAG, "onNetworkChanged----net available :" + NetworkHelper.sharedHelper().isNetworkAvailable());
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    handlenetworkError();
                } else {
                    handleNetworkAvailable();
                }
            }
        };
        NetworkHelper.sharedHelper().addNetworkInductor(networkInductor);
    }

    private void handleNetworkAvailable() {
        robotStateViewUtil.setNetworkAvailable(true);
        robotStateViewUtil.hidden();
        if (robotsViewModel != null) {
            robotsViewModel.getMyRobots();
            String id = MyRobotsLive.getInstance().getRobotUserId();
            if (!TextUtils.isEmpty(id)) {
                MyRobotsLive.getInstance().setSelectToRobotId(MyRobotsLive.getInstance().getRobotUserId());
            }
        }
        enableBtn(true);
    }

    private void handlenetworkError() {
        robotStateViewUtil.setNetworkAvailable(false);
        robotStateViewUtil.showNetworkError();
        binding.setPower(getString(R.string.unknown));
        binding.robotStateValue.setTextColor(getResources().getColor(R.color.robot_normal_color));
        binding.setStatus(getString(R.string.unknown));
        binding.robotStateIcon.setImageLevel(0);
        binding.ivHomeMore.setEnabled(true);
        binding.skillAvatar.setEnabled(false);
        binding.skillGallery.setEnabled(true);
        binding.skillFriend.setEnabled(true);
        binding.robotSwitch.setEnabled(true);
    }

    private void setClickListener() {
        binding.robotSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenSwitchTips();
                showRobotListPopupWindow(view);
            }
        });
        binding.ivHomeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMore(view);
            }
        });

        binding.skillGallery.setOnClickListener(new CheckStateClickListener() {
            @Override
            public void onClick(int id) {
                if (checkPermission("PhotoAlbum")) {
                    PageRouter.toGalaryActivity(getActivity());
                } else {
                    showNotPermissionDialog(getString(R.string.gallery), currentRobot.getMasterUserName(), PERMISSIONCODE_PHOTO);
                }

            }
        });

        binding.skillAvatar.setOnClickListener(new CheckStateClickListener() {
            @Override
            public void onClick(int id) {
                // TODO: 18/4/18  241的getString()方法会报错,因为如果Detach了会造成getResouce()抛异常
            /*    if (!SPUtils.get().getBoolean("open_video", false)) {
                    Toast.makeText(getActivity(), "未开启", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (currentRobot.getOnlineState() != RobotInfo.ROBOT_STATE_ONLINE) {
                    showMsgDialog(null, getString(R.string.avatar_offline));
                    return;
                }
                if (PowerLive.get().isLowPower()) {
                    showMsgDialog(null, getString(R.string.avatar_lowpower));
                    return;
                }
                if (checkPermission("Avatar")) {
                    PageRouter.toAvatarActivity(getActivity());
                } else {
                    showNotPermissionDialog(getString(R.string.video_surveillance), currentRobot.getMasterUserName(), PERMISSIONCODE_AVATAR);
                }
            }
        });
        binding.skillFriend.setOnClickListener(new CheckStateClickListener() {
            @Override
            public void onClick(int id) {
                PageRouter.toMiniFriendActivity(getActivity());
            }
        });
        binding.skillCall.setOnClickListener(new CheckStateClickListener() {
            @Override
            public void onClick(int id) {
                if (currentRobot.getOnlineState() != RobotInfo.ROBOT_STATE_ONLINE) {
                    showMsgDialog(null, getString(R.string.concat_offline));
                    return;
                }
                if (checkPermission("Call")) {
                    LoadingDialog.getInstance(getActivity()).show();
                    robotsViewModel.checkSimState().observe(RobotFragment.this, new Observer<LiveResult>() {
                        @Override
                        public void onChanged(@Nullable LiveResult liveResult) {
                            switch (liveResult.getState()) {
                                case SUCCESS:
                                    liveResult.removeObserver(this);
                                    LoadingDialog.dissMiss();
                                    SimState state = (SimState) liveResult.getData();
                                    if (!state.isHasSim()) {
                                        showMsgDialog(null, getString(R.string.concat_no_sim));
                                    } else {
                                        goToContact(state.getPhoneNum());
                                    }
                                    break;
                                case FAIL:
                                    liveResult.removeObserver(this);
                                    LoadingDialog.dissMiss();
                                    showMsgDialog(null, getString(R.string.concat_enter_fail));
                                    break;
                            }
                        }
                    });
                } else {
                    showNotPermissionDialog(getString(R.string.contact), currentRobot.getMasterUserName(), PERMISSIONCODE_CALL);
                }

            }
        });

        binding.strategyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StrategyActivity.class));
            }
        });

        binding.skillCar.setOnClickListener(new CheckStateClickListener() {
            @Override
            public void onClick(int viewId) {
                PageRouter.toMiniCarActivity(getActivity());
            }
        });
    }

    private void checkRobotExits(){
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setTitle(R.string.bind_tips)
                .setMessage(R.string.bind_message)
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.bind, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                            showMsgDialog(null, getString(R.string.network_invalid_tip));
                            return;
                        }
                        UbtBluetoothManager.getInstance().setChangeWifi(false);
                        UbtBluetoothManager.getInstance().setFromHome(true);
                        if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                                AndPermission.hasPermission(getActivity(), Permission.LOCATION)) {
                            if (UbtBluetoothHelper.getInstance().scanEnd()) {
                                jumpBanding();
                            } else {
                                mJumpBanding = true;
                                ((BaseActivity) getActivity()).showLoadingDialog();
                            }

                        } else {
                            PageRouter.toOpenBluetooth(getActivity());
                        }
                    }
                }).show();

    }

    private void goToContact(final String phoneNum) {
        if (SPUtils.get().getBoolean(MyRobotsLive.getInstance().getRobotUserId() + "contact_privacy_policy", false)) {
            PageRouter.toMiniContactsActivity(getActivity(), phoneNum);
        } else {
            if (contactPrivacyPolicyDialog == null) {
                contactPrivacyPolicyDialog = new MaterialDialog(getActivity());
                contactPrivacyPolicyDialog.setTitle(R.string.concat_privacy_policy);
                contactPrivacyPolicyDialog.setMessage(R.string.concat_privacy_policy_content);
                contactPrivacyPolicyDialog.setPositiveButton(R.string.agree, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contactPrivacyPolicyDialog.dismiss();
                        SPUtils.get().put(MyRobotsLive.getInstance().getRobotUserId() + "contact_privacy_policy", true);
                        PageRouter.toMiniContactsActivity(getActivity(), phoneNum);
                    }
                });
                contactPrivacyPolicyDialog.setNegativeButton(R.string.disagree, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contactPrivacyPolicyDialog.dismiss();
                    }
                });
            }
            contactPrivacyPolicyDialog.show();
        }
    }

    private void showNotPermissionDialog(String title, String masterName, final String code) {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setMessage(getString(R.string.permission_message_hint, title, masterName))
                .setMessageBold(true)
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PermissionUtils.requestPermission(getActivity(), code);
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void initView() {
        final View robotStateView = getActivity().findViewById(R.id.root_state_view);
        ImageView ivRobot = getActivity().findViewById(R.id.iv_robot);
        binding.skillContainer.setOnBackgroundListener(this);
        robotStateViewUtil = new RobotStateViewUtil(robotStateView, ivRobot);
        setClickListener();
        robotStateViewUtil.setListener(new RobotStateViewUtil.StateViewListener() {
            @Override
            public void bind() {
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    showMsgDialog(null, getString(R.string.network_invalid_tip));
                    return;
                }
                UbtBluetoothManager.getInstance().setChangeWifi(false);
                UbtBluetoothManager.getInstance().setFromHome(true);
                if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                        AndPermission.hasPermission(getActivity(), Permission.LOCATION)) {
                    if (UbtBluetoothHelper.getInstance().scanEnd()) {
                        jumpBanding();
                    } else {
                        mJumpBanding = true;
                        ((BaseActivity) getActivity()).showLoadingDialog();
                    }

                } else {
                    PageRouter.toOpenBluetooth(getActivity());
                }
            }

            @Override
            public void connect() {
                RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
                if (currentRobot != null) {
                    reconnect(currentRobot); //TODO
                }
            }

            @Override
            public void netConnect() {
                PageRouter.toSystemNetSetting(getActivity());
            }
        });

    }

    private void jumpBanding() {
        mJumpBanding = false;
        Log.d(TAG, " mJumpBanding");
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(null, getString(R.string.network_invalid_tip));
            return;
        }
        if (UbtBluetoothHelper.getInstance().getPurposeDevice() != null) {
            Intent intent = new Intent(getActivity(), BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
        } else if (UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(getActivity());
        } else {
            PageRouter.toOpenPowerTip(getActivity());
        }
    }

    private void showRobotListPopupWindow(View view) {
        if (robotPopupWindow == null) {
            robotPopupWindow = new RobotPopupWindow(getActivity(), this);
        }
        String currentRobotId = "";
        if (currentRobot != null) {
            currentRobotId = currentRobot.getRobotUserId();
        }
        robotPopupWindow.show(robotInfos, currentRobotId, view, 0, 0);
    }

    private void showRobotPresentPopupWindow(View view) {
        if (robotPresentWindow == null) {
            robotPresentWindow = new RobotPresentWindow(getActivity());
            robotPresentWindow.setPresentListener(new IPresentListener() {
                @Override
                public void onReceive() {
                    toUser = true;
                    TVSManager.getInstance(getActivity()).toUserCenter(MyRobotsLive.getInstance().getRobotEquipmentId());
                    QQMusicStatsImp.getInstance().hidePresent();
                    presentView.toHide();
                }

                @Override
                public void onClose() {
                    QQMusicStatsImp.getInstance().hidePresent();
                    presentView.toHide();
                }
            });
        }
        robotPresentWindow.show(getActivity().getWindow().getDecorView());

    }


    @Override
    public void onSwitch(final RobotInfo robotInfo) {
        if (robotPopupWindow != null) {
            robotPopupWindow.dismiss();
        }
        hiddenRobotReconnectTipUtil();
        robotFragmentAnimator.switchRobotAnimation(robotInfo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NetworkHelper.sharedHelper().removeNetworkInductor(networkInductor);

    }

    private void showMore(View view) {
        if (homeMorePopupWindow == null) {
            homeMorePopupWindow = new HomeMorePopupWindow(getActivity());
        }
        homeMorePopupWindow.show(view, 0, 0);
    }


    @Override
    public void onSkillAnimationStart() {
        binding.setRobotSkillAniStart(true);
    }

    @Override
    public void onTopStateAnimationStart() {
        binding.setRobotStateAniStart(true);
    }

    @Override
    public void onRobotFragAnimationEnd() {
        //第一次进入机器人界面的时候，绑定的弹窗会优先于动画出现，所以初始化Data操作放在Data后去完成
        if (isAdded()) {
            initData();
        }
    }

    @Override
    public void onRobotSwitchAnimationEnd(final RobotInfo robotInfo) {
        robotStateViewUtil.showSwitching();
        mHandler.postDelayed(new Runnable() {//这样做的目的是为了让延长切换的效果(showSwitching)，让用户有直观的感受
            @Override
            public void run() {
                final LiveResult result = robotsViewModel.switchToRobot(robotInfo);
                result.observe(RobotFragment.this, new Observer<LiveResult>() {
                    @Override
                    public void onChanged(@Nullable LiveResult liveResult) {
                        Log.d(TAG, "onSwitch " + liveResult.getState());
                        switch (liveResult.getState()) {
                            case LOADING:
                                enableBtn(false);
                                break;
                            case SUCCESS:
                                robotFragmentAnimator.connectedRobotAnimation();
                                robotStateViewUtil.showSwitchSuccess();
                                liveResult.removeObserver(this);
                                if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
                                    connect2Robot(robotInfo);
                                } else {
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            robotStateViewUtil.showOffLine();
                                        }
                                    }, 2000);
                                }
                                enableBtn(true);
                                break;
                            case FAIL:
                                robotFragmentAnimator.connectedRobotAnimation();
                                if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
                                    connect2Robot(robotInfo);
                                } else {
                                    robotStateViewUtil.showSwitchSuccess();
                                }
                                liveResult.removeObserver(this);
                                enableBtn(false);
                                break;
                        }
                    }
                });
            }
        }, SWITCHING_TIME);
    }


    @Override
    public void onPageStepListener(float percent) {
        if (binding.ivGaosi.getVisibility() == View.VISIBLE) {
            binding.ivGaosi.setAlpha(percent);
            ((MainActivity) getActivity()).onPageStepListener(percent);
        }
        if (percent > 0.95) {
            ((MainActivity) getActivity()).setForbidScoll(true);
        } else {
            ((MainActivity) getActivity()).setForbidScoll(false);
        }
    }

    @Override
    public void dismissBackGround() {
        binding.ivGaosi.setVisibility(View.GONE);
        ((MainActivity) getActivity()).dismissMenuBlurBackground();
    }

    public void startBottomAnimation() {
        binding.skillContainer.setScroll2Bottom();
    }

    /**
     *
     * @param hasBroad
     */
    public void setUpBlurBitmapIfNull(boolean hasBroad) {
        if (hasBroad) {
            if (mBlurBitmapMap.get(ROBOT_WITH_BORAD_KEY) == null) {
                final boolean isSwitchShow = binding.switchTips.getVisibility() == View.VISIBLE;
                if(isSwitchShow){
                    binding.switchTips.setVisibility(View.GONE);
                }
                final boolean isPresentShow = binding.present.getVisibility() == View.VISIBLE;
                if (isPresentShow){
                    binding.present.setVisibility(View.INVISIBLE);
                }
                RobotFragmentBlurHelper.screenShot(binding.rlScreenShotContainer, new RobotFragmentBlurHelper.IScreenShotCallBack() {
                    @Override
                    public void onGenerateScreenBitmap(Bitmap screenBitmap) {
                        if (isSwitchShow){
                            binding.switchTips.setVisibility(View.VISIBLE);
                        }
                        if (isPresentShow){
                            binding.present.setVisibility(View.VISIBLE);
                        }
                        setUpBlurBitmap(screenBitmap, true);
                    }
                });
            }
        } else {

            if (mBlurBitmapMap.get(ROBOT_WITHOUT_BORAD_KEY) == null) {
                final boolean isSwitchShow = binding.switchTips.getVisibility() == View.VISIBLE;
                if(isSwitchShow){
                    binding.switchTips.setVisibility(View.GONE);
                }
                final boolean isPresentShow = binding.present.getVisibility() == View.VISIBLE;
                if (isPresentShow){
                    binding.present.setVisibility(View.INVISIBLE);
                }
                RobotFragmentBlurHelper.screenShot(binding.rlScreenShotContainer, new RobotFragmentBlurHelper.IScreenShotCallBack() {
                    @Override
                    public void onGenerateScreenBitmap(Bitmap screenBitmap) {
                        if (isSwitchShow){
                            binding.switchTips.setVisibility(View.VISIBLE);
                        }
                        if (isPresentShow){
                            binding.present.setVisibility(View.VISIBLE);
                        }
                        setUpBlurBitmap(screenBitmap, false);
                    }
                });
            }
        }
    }


    private void setUpBlurBitmap(Bitmap screenBitmap, final boolean isWithBorad) {
        BlurUtil.blurNativelyPixelsHelper(screenBitmap, 80, false, new BlurUtil.IBlurCallBack() {
            @Override
            public void onCreateBlurBitmap(final Bitmap newBitmap) {
                if (newBitmap == null) {
                    Log.e(TAG, "onCreateBlurBitmap: Screen shot Bitmap is null");
                    return;
                }
                if (isWithBorad) {
                    mBlurBitmapMap.put(ROBOT_WITH_BORAD_KEY, newBitmap);
                } else {
                    mBlurBitmapMap.put(ROBOT_WITHOUT_BORAD_KEY, newBitmap);
                }
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.ivGaosi.setImageBitmap(newBitmap);
                        }
                    });
                }
            }

            @Override
            public void onFailed(String errorMsg) {
                Log.e(TAG, "errorMsg: " + errorMsg);
            }
        });
    }

    @Override
    public void trigerShowUpBackground() {
        binding.ivGaosi.setVisibility(View.VISIBLE);
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        Bitmap bitmap;
        if (robotInfo != null && robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            bitmap = mBlurBitmapMap.get(ROBOT_WITHOUT_BORAD_KEY);
        } else {
            bitmap = mBlurBitmapMap.get(ROBOT_WITH_BORAD_KEY);
        }
        if (bitmap != null) {
            binding.ivGaosi.setImageBitmap(bitmap);
            ((MainActivity) getActivity()).showMenuBlurBackground();
        }

    }

    private abstract class CheckStateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!CollectionUtils.isEmpty(robotInfos)) {
                if (currentRobot != null) {
                    onClick(view.getId());
                }
            } else {
                checkRobotExits();
            }
        }

        public abstract void onClick(int viewId);

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyRobotsLive.getInstance().getRobots().removeObserver(robotsObserver);
        MyRobotsLive.getInstance().getCurrentRobot().removeObserver(currentRobotObserver);
        MyRobotsLive.getInstance().getSelectToRobot().removeObserver(selectToRobotObserver);
        QQMusicStatsImp.getInstance().removeObserver(qqMusicStatsImpObserver);
    }

    private Observer<LiveResult> robotsObserver = new Observer<LiveResult>() {
        @Override
        public void onChanged(LiveResult liveResult) { //观察机器人列表变化情况
            Log.d(TAG, "robotsObserver state = " + liveResult.getState());
            List<RobotInfo> robotInfos = (List<RobotInfo>) liveResult.getData();
            switch (liveResult.getState()) {
                case LOADING:
                    LoadingDialog.getInstance(getActivity()).show();
                    break;
                case SUCCESS:
                    LoadingDialog.dissMiss();
                    loadRobotListSuccess(robotInfos);
                    break;
                case FAIL:
                    LoadingDialog.dissMiss();
                    loadRobotListFail();
                    break;
            }
        }
    };

    private void loadRobotListSuccess(List<RobotInfo> robotInfos) {
        isLoadSuc = true;
        this.robotInfos = robotInfos;
        Log.d(TAG, "loadRobotListSuccess");
        Log.d(TAG, " list : " + CollectionUtils.isEmpty(robotInfos));
        if (CollectionUtils.isEmpty(robotInfos)) {
            robotStateViewUtil.showBinding();
            binding.setRobotCount(0);
            binding.ivHomeMore.setVisibility(View.INVISIBLE);
            startScan();
            setUpBlurBitmapIfNull(true);
            hiddenSwitchTips();
            hiddenRobotReconnectTipUtil();
        } else {
            Log.d(TAG, "loadRobotListSuccess robotInfos = " + robotInfos.size());
            binding.setRobotCount(robotInfos.size());
            binding.ivHomeMore.setVisibility(View.VISIBLE);

            if (robotInfos.size() > 1) {
                if (!switchTipsShowed) {
                    showSwitchTips();
                }
            }else{
                hiddenSwitchTips();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
        stopScan();
    }

    private void startScan() {
        if (isSelect && isLoadSuc && CollectionUtils.isEmpty(MyRobotsLive.getInstance().getRobots().getData())) {
            if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                    AndPermission.hasPermission(getActivity(), Permission.LOCATION)) {
                UbtBluetoothHelper.getInstance().startScan(MyRobotsLive.getInstance().getRobotUserId());
                UbtBluetoothHelper.getInstance().setBluetoothScanListener(new UbtBluetoothHelper.BluetoothScanListener() {
                    @Override
                    public void scanEnd() {
                        if (mJumpBanding) {
                            jumpBanding();
                            ((BaseActivity) getActivity()).dismissDialog();
                        }
                    }

                });
                startScanBle = true;
            }
        }
    }
    private void stopScan() {
        if (startScanBle) {
            UbtBluetoothHelper.getInstance().stopScan();
        }
    }

    private void reconnect(final RobotInfo robotInfo) {
        final LiveResult result = robotsViewModel.reConnectRobot(robotInfo);
        handleConnectResult(robotInfo, result, true);
    }

    private void connect2Robot(final RobotInfo robotInfo) {
        Log.d(TAG, "connect2Robot " + robotInfo.getRobotUserId());
        final LiveResult<RobotInfo> result = robotsViewModel.connectToRobot(robotInfo);
        if (!robotInfo.getRobotUserId().equals(MyRobotsLive.getInstance().getRobotUserId())) {
            MyRobotsLive.getInstance().selectRobot(robotInfo.getUserId());
        }
        handleConnectResult(robotInfo, result, false);

    }

    private void handleConnectResult(final RobotInfo robotInfo, LiveResult result, final boolean isReconnect) {
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                Log.d(TAG, "connect2Robot result" + liveResult.getState());
                RobotInfo newRobotInfo = MyRobotsLive.getInstance().getRobotById(robotInfo.getRobotUserId());
                if(MyRobotsLive.getInstance().getRobotCount() == 0 || newRobotInfo == null){ //如果已经解绑了就不显示结果了
                    Log.d(TAG,"handleConnectResult robotList is null");
                    enableBtn(true);
                    hiddenRobotReconnectTipUtil();
                    return;
                }
                switch (liveResult.getState()) {
                    case LOADING:
                        robotStateViewUtil.showConnectting();
                        connecting(isReconnect);
                        setUpBlurBitmapIfNull(true);
                        break;
                    case SUCCESS:
                        robotStateViewUtil.showConnectSuccess();
                        connectSuccess(isReconnect);
                        liveResult.removeObserver(this);
                        NotificationCenter.defaultCenter().unsubscribe(RobotImResponseEvent.class, robotImResponseSubscriber);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUpBlurBitmapIfNull(false);
                            }
                        }, 3200);
                        break;
                    case FAIL:
                        if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
                            robotStateViewUtil.showConnectFail();
                            connectFail(isReconnect);
                        } else {
                            robotStateViewUtil.showOffLine();
                            connectOffline(isReconnect);
                        }
                        liveResult.removeObserver(this);
                        setUpBlurBitmapIfNull(true);
                        break;
                }
            }
        });
    }

    private void connectFail(boolean isReconnect) {
        enableBtn(false);
        binding.skillGallery.setEnabled(true);
        binding.robotSwitch.setEnabled(true);
        binding.ivHomeMore.setEnabled(true);
        if (isReconnect) {
            showRobotReconnectTipUtil(false);
        }

    }

    private void connecting(boolean isReconnect) {
        enableBtn(false);
        hiddenRobotReconnectTipUtil();
    }

    private void connectSuccess(boolean isReconnect) {
        enableBtn(true);
        hiddenRobotReconnectTipUtil();
    }

    private void connectOffline(boolean isReconnect) {
        enableBtn(true);
        if (isReconnect) {
            showRobotReconnectTipUtil(true);
        }
    }

    public void loadRobotListFail() {
        Log.d(TAG, "loadRobotListFail");
        robotStateViewUtil.showNetworkError();
    }

    /**
     * 当前选中机器人状态监听
     */
    private Observer<RobotInfo> currentRobotObserver = new Observer<RobotInfo>() {
        @Override
        public void onChanged(@Nullable RobotInfo robotInfo) {
            refreshCurrentRobot(robotInfo);
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void refreshCurrentRobot(RobotInfo robotInfo) {
        Log.d(TAG, "refreshCurrentRobot: " + robotInfo);
        currentRobot = robotInfo;
        if (robotInfo != null) {
            binding.robotStateIcon.setImageLevel(robotInfo.getOnlineState() + 1);
            binding.robotStateValue.setTextColor(getResources().getColor(R.color.robot_normal_color));
            switch (robotInfo.getOnlineState()) {
                case RobotInfo.ROBOT_STATE_OFFLINE:
                    binding.setStatus(getString(R.string.offline));
                    binding.robotStateValue.setTextColor(getResources().getColor(R.color.robot_offline_color));
                    robotStateViewUtil.showOffLine();
                    NotificationCenter.defaultCenter().subscriber(RobotImResponseEvent.class, robotImResponseSubscriber);
                    break;
                case RobotInfo.ROBOT_STATE_ONLINE:
                    setUpBlurBitmapIfNull(false);
                    binding.setStatus(getString(R.string.online));
                    break;
                case RobotInfo.ROBOT_STATE_UNAVAILABLE:
                    binding.setStatus(getString(R.string.offline));
                    robotStateViewUtil.showOffLine();
                    binding.setStatus(getString(R.string.unknown));
                    break;
            }
        } else {
            binding.setStatus(getString(R.string.unknown));
        }
    }


    /**
     * 切换机器人事件监听
     */
    private Observer<RobotInfo> selectToRobotObserver = new Observer<RobotInfo>() {
        @Override
        public void onChanged(@Nullable RobotInfo robotInfo) {
            Log.d(TAG, "selectToRobotObserver = " + robotInfo);
            if (robotInfo != null) {
                connect2Robot(robotInfo);
            }
        }
    };

    public void enableBtn(boolean enable) {
        binding.ivHomeMore.setEnabled(enable);
        binding.skillAvatar.setEnabled(enable);
        binding.skillGallery.setEnabled(enable);
        binding.skillFriend.setEnabled(enable);
        binding.skillCall.setEnabled(enable);
        binding.robotSwitch.setEnabled(enable);
    }

    private void showSwitchTips() {
        switchTipsShowed = true;
        SPUtils.get().put(SWITCH_TIPS + AuthLive.getInstance().getUserId(), true);
        if (binding.switchTips != null) {
            binding.switchTips.setVisibility(View.VISIBLE);
            binding.switchTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hiddenSwitchTips();
                }
            });
        }
    }

    private void hiddenSwitchTips() {
        if (binding.switchTips != null) {
            binding.switchTips.setVisibility(View.INVISIBLE);
        }
    }


    public Subscriber<RobotImResponseEvent> robotImResponseSubscriber = new Subscriber<RobotImResponseEvent>() {
        @Override
        public void onEvent(RobotImResponseEvent event) {
            if (event.robotId != null && currentRobot != null) {
                if (event.robotId.equals(currentRobot.getRobotUserId()) && currentRobot.getOnlineState() != RobotInfo.ROBOT_STATE_ONLINE) {
                    MyRobotsLive.getInstance().postUpdateRobotState(event.robotId, RobotInfo.ROBOT_STATE_ONLINE);
                }
            }
        }
    };

    private void showRobotReconnectTipUtil(boolean offline) {
        if (robotReconnectTipUtil == null) {
            robotReconnectTipUtil = new RobotReconnectTipUtil(binding.switchWifiTipsLay, new RobotReconnectTipUtil.BtnClickListener() {
                @Override
                public void onSwitchWifi() {
                    PageRouter.toSettingShowRobotWifiActivity(getActivity());
                }
            });
        }
        if (offline) {
            robotReconnectTipUtil.showOffline();
        } else {
            robotReconnectTipUtil.showDisconnect();
        }
    }

    private void hiddenRobotReconnectTipUtil() {
        if (robotReconnectTipUtil != null) {
            robotReconnectTipUtil.hidden();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.d(TAG, " onShow isSelect : " + isSelect);
            if(!isSelect) {
                isSelect = true;
                startScan();
            }


        }else {
            Log.d(TAG, " onHide isSelect : " + isSelect);
            if(isSelect) {
                stopScan();
                isSelect = false;
            }
        }
    }

    public void onShow() {

    }

    public void onHide() {

    }
}

