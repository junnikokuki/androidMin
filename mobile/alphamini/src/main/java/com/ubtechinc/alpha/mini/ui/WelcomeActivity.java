package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;
import com.ubtechinc.alpha.mini.viewmodel.MobileViewModel;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;

public class WelcomeActivity extends BaseActivity {

    private AuthViewModel authViewModel;
    private RobotsViewModel robotsViewModel;
    private MobileViewModel mobileViewModel;
    private NetworkHelper.NetworkInductor inductor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .fitsSystemWindows(false)
                .init();
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        authViewModel = new AuthViewModel();
        robotsViewModel = RobotsViewModel.get();
        mobileViewModel = new MobileViewModel();
        if (!authViewModel.checkToken(this)) {
            handler.sendEmptyMessageDelayed(0, 3000);
        } else {
            if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                handler.sendEmptyMessageDelayed(1, 3000);
                authViewModel.logoutTVS();
                return;
            }
            handler.sendEmptyMessageDelayed(1, 15000);
            AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
                @Override
                public void onChanged(@Nullable AuthLive authLive) {
                    switch (authLive.getState()) {
                        case LOGINED:
                            handler.removeMessages(1);
                            checkoutMobileInfo();
                            break;
                        case ERROR:
                            handler.removeMessages(1);
                            PageRouter.toLogin(WelcomeActivity.this);
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        inductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                LogUtils.d(TAG, "onNetworkChanged----net available :" + NetworkHelper.sharedHelper().isNetworkAvailable());
                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    ToastUtils.showShortToast(R.string.network_disable);
                }
            }
        };
        /*MyRobotsLive.getInstance().getRobots().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                if (!SPUtils.get().getBoolean(MainActivity.SHOW_BINDING_GUIDE + AuthLive.getInstance().getUserId())) {
                    if (AuthLive.AuthState.LOGINED == AuthLive.getInstance().getState()) {
                        switch (liveResult.getState()) {
                            case SUCCESS:
                                if (MyRobotsLive.getInstance().getRobotCount() > 0) {
                                    PageRouter.toMain(WelcomeActivity.this);
                                } else {
                                    if (!SPUtils.get().getBoolean(MainActivity.SHOW_BINDING_GUIDE + AuthLive.getInstance().getUserId())) {
                                        UbtBluetoothManager.getInstance(WelcomeActivity.this).setChangeWifi(false);
                                        PageRouter.toBangdingActivity(WelcomeActivity.this);
                                    } else {
                                        PageRouter.toMain(WelcomeActivity.this);
                                    }
                                }
                                MyRobotsLive.getInstance().getRobots().removeObserver(this);
                                finish();
                                break;
                            case FAIL:
                                PageRouter.toMain(WelcomeActivity.this);
                                MyRobotsLive.getInstance().getRobots().removeObserver(this);
                                finish();
                                break;
                        }
                    }
                }
            }
        });*/
    }

    private void checkoutMobileInfo() {
        mobileViewModel.getBindMobileInfo().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveRessult = (LiveResult) o;
                switch (liveRessult.getState()) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        handler.removeMessages(1);
                        mobileViewModel.getBindMobileInfo().removeObserver(this);
                        String phone = (String) liveRessult.getData();
                        if (TextUtils.isEmpty(phone)) {
                            PageRouter.toLogin(WelcomeActivity.this);
                            finish();
                        } else {
                            robotsViewModel.getMyRobots();
                            PageRouter.toMain(WelcomeActivity.this);
                            finish();
                        }
                        break;
                    case FAIL:
                        handler.removeMessages(1);
                        mobileViewModel.getBindMobileInfo().removeObserver(this);
                        PageRouter.toLogin(WelcomeActivity.this);
                        finish();
                        break;
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                toastError(getString(R.string.net_work_error));
            }
            PageRouter.toLogin(WelcomeActivity.this);
//            PageRouter.toMain(WelcomeActivity.this);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
        handler.removeCallbacks(null);
        handler = null;
        NetworkHelper.sharedHelper().removeNetworkInductor(inductor);
    }
}
