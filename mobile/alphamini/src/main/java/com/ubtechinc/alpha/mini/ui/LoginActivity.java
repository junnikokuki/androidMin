package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.gyf.barlibrary.ImmersionBar;

import com.ubtech.utilcode.utils.LogUtils;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;
import com.ubtechinc.alpha.mini.viewmodel.MobileViewModel;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.UbtBluetoothManager;

public class LoginActivity extends BaseActivity {

    AuthViewModel authViewModel;
    RobotsViewModel robotsViewModel;
    MobileViewModel mobileViewModel;

    private AuthLive.AuthState state;

    MaterialDialog forceOfflineDialog;
    MaterialDialog wxFailDialog;
    MaterialDialog forbiddenDialog;

    RadioGroup netSwitch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        ImmersionBar.with(this).transparentStatusBar()
                .statusBarDarkFont(false)
                .fitsSystemWindows(false).init();
        authViewModel = new AuthViewModel();
        robotsViewModel = RobotsViewModel.get();
        mobileViewModel = new MobileViewModel();
        View wxLoginBtn = findViewById(R.id.wx_login_btn);
        View qqLoginBtn = findViewById(R.id.qq_login_btn);
/*         netSwitch = findViewById(R.id.net_switch);
       if(BuildConfig.DEBUG){
            netSwitch.setVisibility(View.VISIBLE);
            boolean isOuter = SPUtils.get().getBoolean("net_outer",false);
            if(isOuter){
                HostUtil.toOutterNet();
                netSwitch.check(R.id.net_outer);
            }else{
                HostUtil.toInnerNet();
                netSwitch.check(R.id.net_inner);
            }
        }
                netSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.net_inner:
                        HostUtil.toInnerNet();
                        SPUtils.get().put("net_outer",false);
                        break;
                    case R.id.net_outer:
                        HostUtil.toOutterNet();
                        SPUtils.get().put("net_outer",true);
                        break;
                }
            }
        });
*/
        wxLoginBtn.setOnClickListener(btnClick);
        qqLoginBtn.setOnClickListener(btnClick);

        AuthLive.getInstance().observe(this, new Observer<AuthLive>() {
            @Override
            public void onChanged(@Nullable AuthLive authLive) {
                state = authLive.getState();
                LogUtils.d("loginState1 = " + state.name());
                switch (state) {
                    case LOGINED:
                        checkMobileInfoBind();
                        break;
                    case ERROR:
                        dismissDialog();
                        toastError("登录失败");
                        break;
                    case FORCE_OFFLINE:
                        showForceDialog();
                        break;
                    case FORBIDDEN:
                        showForbiddenDialog();
                        break;
                    case LOGINING:
                        showLoadingDialog();
                        break;
                    case CANCEL:
                        dismissDialog();
                        break;
                    case NORMAL:
                        dismissDialog();
                        if (forceOfflineDialog != null) {
                            forceOfflineDialog.dismiss();
                        }
                        break;
                    default:
                        dismissDialog();
                        if (forceOfflineDialog != null) {
                            forceOfflineDialog.dismiss();
                        }
                        break;
                }
            }
        });

       /* MyRobotsLive.getInstance().getRobots().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                if (AuthLive.AuthState.LOGINED == state) {
                    if (!SPUtils.get().getBoolean(MainActivity.SHOW_BINDING_GUIDE + AuthLive.getInstance().getUserId())) {
                        switch (liveResult.getState()) {
                            case SUCCESS:
                                if (MyRobotsLive.getInstance().getRobotCount() > 0) {
                                    PageRouter.toMain(LoginActivity.this);
                                } else {
                                    UbtBluetoothManager.getInstance(LoginActivity.this).setChangeWifi(false);
                                    PageRouter.toBangdingActivity(LoginActivity.this);
                                }
                                dismissDialog();
                                MyRobotsLive.getInstance().getRobots().removeObserver(this);
                                finish();
                                break;
                            case FAIL:
                                PageRouter.toMain(LoginActivity.this);
                                dismissDialog();
                                MyRobotsLive.getInstance().getRobots().removeObserver(this);
                                finish();
                                break;
                        }
                    }
                }
            }
        });*/
    }

    private void showWxFailDialog(boolean install) {
        if (wxFailDialog == null) {
            wxFailDialog = new MaterialDialog(this);
            wxFailDialog.setTitle(R.string.tips);
            wxFailDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuthLive.getInstance().reset();
                    wxFailDialog.dismiss();
                }
            });
        }
        if (install) {
            wxFailDialog.setMessage(R.string.wx_not_support);
        } else {
            wxFailDialog.setMessage(R.string.wx_not_install);
        }
        wxFailDialog.show();
    }

    private void showForbiddenDialog() {
        forbiddenDialog = new MaterialDialog(this);
        forbiddenDialog.setMessage(R.string.forbidden);
        forbiddenDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthLive.getInstance().reset();
                forbiddenDialog.dismiss();
            }
        });
        forbiddenDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AuthLive.getInstance().reset();
            }
        });
        forbiddenDialog.show();
    }

    private void checkMobileInfoBind() {
        mobileViewModel.getBindMobileInfo().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult result = (LiveResult) o;
                switch (result.getState()) {
                    case FAIL:
                        result.removeObserver(this);
                        toastError("登录失败");
                        break;
                    case SUCCESS:
                        result.removeObserver(this);
                        String phone = (String) result.getData();
                        if (TextUtils.isEmpty(phone)) {
                            PageRouter.toMobileNumber(LoginActivity.this);
                        } else {
                            UbtBluetoothManager.getInstance().setChangeWifi(false);
                            robotsViewModel.getMyRobots();
                            PageRouter.toMain(LoginActivity.this);
                        }
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }


    private void showForceDialog() {
        forceOfflineDialog = new MaterialDialog(this);
        forceOfflineDialog.setMessage(R.string.force_offline);
        forceOfflineDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthLive.getInstance().reset();
                forceOfflineDialog.dismiss();
            }
        });
        forceOfflineDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AuthLive.getInstance().reset();
            }
        });
        forceOfflineDialog.show();
    }


    private AnalysisClickListener btnClick = new AnalysisClickListener() {
        @Override
        public void onClick(View view, boolean reported) {
            if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                ToastUtils.showShortToast(R.string.network_disable);
                return;
            }
            switch (view.getId()) {
                case R.id.wx_login_btn:
                    if (!authViewModel.isWXInstall()) { //未安装微信客户端
                        showWxFailDialog(false);
                        return;
                    } else if (!authViewModel.isWXSupport()) {
                        showWxFailDialog(true);
                        return;
                    }
                    authViewModel.loginWX(LoginActivity.this);
                    break;
                case R.id.qq_login_btn:
                    authViewModel.loginQQ(LoginActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authViewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (state != null) {
            switch (state) {
                case LOGINING:
                    showLoadingDialog();
                    break;
            }
        }
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                Log.d(TAG, "inputMethodManager.isActive()" + inputMethodManager.isActive());

                if (inputMethodManager.isActive()) {
                    getWindow().getDecorView().requestFocus();
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        }, 200);

        if (authViewModel != null) {

            authViewModel.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        if (forceOfflineDialog != null && forceOfflineDialog.isShowing()) {
            forceOfflineDialog.dismiss();
        }
        if (AuthLive.getInstance().getState() != AuthLive.AuthState.LOGINED) {
            AuthLive.getInstance().reset();
        }
        super.onDestroy();
    }
}
