package com.ubtechinc.alpha.mini.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.component.http.CookieInterceptor;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.push.AlphaMiniPushConfig;
import com.ubtechinc.alpha.mini.push.AlphaMiniPushManager;
import com.ubtechinc.alpha.mini.repository.TVSAuthRepository;
import com.ubtechinc.alpha.mini.repository.UBTAuthRepository;
import com.ubtechinc.alpha.mini.tvs.entity.LoginInfo;
import com.ubtechinc.alpha.mini.entity.UserInfo;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.nets.im.business.LoginBussiness;

public class AuthViewModel implements TVSAuthRepository.AuthCallBack, UBTAuthRepository.UBTAuthCallBack {

    public static final String TAG = "AuthViewModel";

    AuthLive authLive = AuthLive.getInstance();

    private TVSAuthRepository tvsAuthRepository;
    private UBTAuthRepository ubtAuthRepository;

    public AuthViewModel() {
        tvsAuthRepository = new TVSAuthRepository();
        ubtAuthRepository = new UBTAuthRepository();
    }

    public void loginWX(Activity activity) {
        authLive.logining();
        tvsAuthRepository.loginWX(activity, this);
    }

    public boolean isWXInstall(){
        return tvsAuthRepository.isWXInstall();
    }

    public boolean isWXSupport(){
        return tvsAuthRepository.isWXSupport();
    }

    public void loginQQ(Activity activity) {
        authLive.logining();
        tvsAuthRepository.loginQQ(activity, this);
    }

    public void logoutTVS(){
        tvsAuthRepository.logout();
    }

    public void logout(boolean forceOffline) {
        LogUtils.D("logout force = " + forceOffline);
        tvsAuthRepository.logout();
        LoginBussiness.getInstance(AlphaMiniApplication.getInstance()).logout();
        AlphaMiniPushManager.getInstance().unRegisterPush();
        if(!forceOffline){
            ubtAuthRepository.logout(this);
        }
        CookieInterceptor.get().setToken("");
        CookieInterceptor.get().setExpireAt(-1l);
        if(!forceOffline){
            authLive.logout();
        }
        MyRobotsLive.getInstance().clear();
        //FIXME -logic.peng 登出后,应取消蓝牙自动配网--主动停止--地方2
        UbtBluetoothManager.getInstance().setAutoConnect(false);
        UbtBluetoothManager.getInstance().closeConnectBle();
    }

    public boolean checkToken(Activity activity) {
        if (!isTokenExist(activity)) {
            return false;
        } else {
            tvsAuthRepository.refreshLogin(activity, this);
        }
        return true;
    }

    public boolean isTokenExist(Activity activity) {
        return tvsAuthRepository.isTokenExist(activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tvsAuthRepository.onActivityResult(requestCode, resultCode, data);
    }

    public void onResume() {
        tvsAuthRepository.onResume();
    }

    @Override
    public void onTVSLoginSuccess(LoginInfo loginInfo) {
        ubtAuthRepository.login(loginInfo, this);
    }

    @Override
    public void onSuccess(final UserInfo userInfo) {
        Phone2RobotMsgMgr.getInstance().init(userInfo.getUserId(),new Phone2RobotMsgMgr.ImLoginListener() {
            @Override
            public void onSuccess() {
                AlphaMiniPushManager.getInstance().setPushConfig(new AlphaMiniPushConfig(CookieInterceptor.get().getToken(), userInfo.getUserId()));
                AlphaMiniPushManager.getInstance().registerPush();
                AuthLive.getInstance().logined(userInfo);
            }

            @Override
            public void onError() {
                AuthLive.getInstance().error();
            }
        });

    }

    @Override
    public void onError() {
        tvsAuthRepository.logout();
        authLive.error();
    }

    @Override
    public void onCancel() {
        authLive.cancel();
    }

    @Override
    public void onLogout() {
        authLive.logout();
        AlphaMiniPushManager.getInstance().unRegisterPush();
    }

    @Override
    public void onLogoutError() {
        Log.i(TAG, "onLogoutError: 登出失败");
    }

}
