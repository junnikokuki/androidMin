package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.GetRobotConfiguration;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

/**
 * @author：wululin
 * @date：2017/12/26 16:53
 * @modifier：lulin.wu
 * @modify_date：2017/12/26 16:53
 * [A brief description]
 * version
 */

public class SettingShowRobotWifiActivity extends BaseToolbarActivity implements View.OnClickListener{
    private TextView mChooseRobotWifi;
    private TextView mSettingRobotWifi;
    private TextView mWifiNameTv;

    private ViewStub mVbSimple;
    private ViewStub mVbDisconnected;
    private ViewStub mVbMobileDisConnect;
    private ViewStub mVbConnectFailed;

    private View     mSimpleView;
    private View     mDisconnectedView;
    private View     mMobileDisconnect;
    private View     mConntectFailed;
    private boolean startScanBle;
    private boolean mJumpBanding;
    private String wifiName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_show_robot_wifi_layout);
        initView();
        wifiName = getIntent().getStringExtra(PageRouter.INTENT_KEY_WIFINAME);
        bleOrNetworkChangeRobotWifi();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void bleOrNetworkChangeRobotWifi() {
        RobotInfo mRobotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if(Utils.isNetworkAvailable(this)){
            if(mRobotInfo != null && mRobotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE){
                //如果从其他页面传入的wifiName == null 或者 "获取中..." ,这时候其实是
                //FIXME logic.peng 如果机器人在线,且获取到了机器人连接的WIFI名, 说明IM在线,走IM切WIFI, 其他情况走蓝牙
                if (wifiName != null && !wifiName.equals(getString(R.string.getting_robot_wifi))) {
                    Log.w(TAG, "wifiName = " + wifiName);
                    mWifiNameTv.setText(wifiName);//默认显示IM切网
                }
            }else {
                showDisconnectedLayout();//显示蓝牙配网
            }
        }else {
            showMobileDisconnectLayout();
        }
    }

    private void initView(){
        View toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar,getString(R.string.setting_robot_wifi),View.GONE,false);

        mVbSimple           = findViewById(R.id.vb_network_simple);
        mVbDisconnected     = findViewById(R.id.vb_network_no_wifi);
        mVbMobileDisConnect = findViewById(R.id.vb_network_mobile_disconnect);
        mVbConnectFailed    = findViewById(R.id.vb_network_connected_failed);

        showSimpleLayout();
    }

    private void dismissLayout(){
        mVbSimple.setVisibility(View.GONE);
        mVbDisconnected.setVisibility(View.GONE);
        mVbMobileDisConnect.setVisibility(View.GONE);
        mVbConnectFailed.setVisibility(View.GONE);
    }

    private void showSimpleLayout(){
        dismissLayout();
        if (mSimpleView == null){
            mVbSimple.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    mWifiNameTv      = inflated.findViewById(R.id.wifi_name_tv);
                    mChooseRobotWifi = inflated.findViewById(R.id.btn_choose_wifi);

                    mChooseRobotWifi.setOnClickListener(SettingShowRobotWifiActivity.this);
                }
            });
            mSimpleView = mVbSimple.inflate();
        }
        mVbSimple.setVisibility(View.VISIBLE);
    }

    private void showDisconnectedLayout(){
        dismissLayout();
        if (mDisconnectedView == null){
            mVbDisconnected.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    mSettingRobotWifi = inflated.findViewById(R.id.btn_setting_wifi);

                    mSettingRobotWifi.setOnClickListener(SettingShowRobotWifiActivity.this);
                }
            });
            mDisconnectedView = mVbDisconnected.inflate();
        }
        mVbDisconnected.setVisibility(View.VISIBLE);
    }

    private void showMobileDisconnectLayout(){
        dismissLayout();
        if (mMobileDisconnect == null){
            mMobileDisconnect = mVbMobileDisConnect.inflate();
        }
        mVbMobileDisConnect.setVisibility(View.VISIBLE);
    }

    private void showConnectedFailedLayout(){
        dismissLayout();
        if (mConntectFailed == null){
            mConntectFailed = mVbConnectFailed.inflate();
        }
        mVbConnectFailed.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_wifi:
                PageRouter.toSettingChooseWifiActivity(SettingShowRobotWifiActivity.this);
                break;
            case R.id.btn_setting_wifi:
                if(!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    showMsgDialog(getString(R.string.network_invalid_tip));
                    return;
                }

                UbtBluetoothManager.getInstance().setChangeWifi(true);
                if (UbtBluetoothManager.getInstance().isOpenBluetooth() && AndPermission.hasPermission(this, Permission.LOCATION)) {
                    if (UbtBluetoothHelper.getInstance().scanEnd()) {
                        jumpBanding();
                    } else {
                        mJumpBanding = true;
                        showLoadingDialog();
                    }
                } else {
                    PageRouter.toOpenBluetooth(this);
                }
                break;
        }
    }

    private void jumpBanding() {
        mJumpBanding = false;
        Log.d(TAG, " mJumpBanding");
        if(!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if(UbtBluetoothHelper.getInstance().getPurposeDevice() != null && UbtBluetoothHelper.getInstance().getPurposeDevice().getSn().equals(MyRobotsLive.getInstance().getRobotUserId())) {
            Intent intent = new Intent(this, BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
        } else if(UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(this);
        } else {
            PageRouter.toOpenPowerTip(this);
        }
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        bleOrNetworkChangeRobotWifi();
    }

    ICallback<GetRobotConfiguration.GetRobotConfigurationResponse> mICallback = new ICallback<GetRobotConfiguration.GetRobotConfigurationResponse>() {
        @Override
        public void onSuccess(final GetRobotConfiguration.GetRobotConfigurationResponse data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChooseRobotWifi.setEnabled(true);
                    mWifiNameTv.setText(data.getWifiname().replace("\"",""));
                }
            });
        }

        @Override
        public void onError(ThrowableWrapper e) {
            //获取失败,线程中执行
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showConnectedFailedLayout();
                }
            });
        }
    };


}
