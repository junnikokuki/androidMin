package com.ubtechinc.alpha.mini.ui.bluetooth.bean;

import com.ubtechinc.alpha.mini.ui.bluetooth.BangdingManager;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;

/**
 * @author：wululin
 * @date：2017/11/3 15:07
 * @modifier：ubt
 * @modify_date：2017/11/3 15:07
 * [A brief description]
 * version
 */

public  class  BandingListenerAbster implements BangdingManager.BanddingListener {

    @Override
    public void connWifiSuccess() {

    }

    @Override
    public void onSuccess(RegisterRobotModule.Response response) {

    }

    @Override
    public void onFaild(int errorCode) {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void devicesConnectByOther() {

    }

    @Override
    public void bindByOthers(CheckBindRobotModule.User user) {

    }

    @Override
    public void robotNotWifi() {

    }

    @Override
    public void connectFailed() {

    }
}
