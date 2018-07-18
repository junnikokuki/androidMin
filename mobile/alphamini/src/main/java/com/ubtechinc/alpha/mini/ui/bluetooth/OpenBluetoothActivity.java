package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.OpenBluetoothEvent;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

/**
 * @author：wululin
 * @date：2017/10/13 11:14
 * @modifier：ubt
 * @modify_date：2017/10/13 11:14
 * [A brief description]
 * 开启蓝牙界面
 */

public class OpenBluetoothActivity extends BleNetworkBaseActivity {
    private static final String TAG = OpenBluetoothActivity.class.getSimpleName();
    private TextView mAllowLocationTv;
    private TextView mAllowLocationBtn;
    private TextView mAllowBlueTv;
    private TextView mAllowBlueBtn;
    private ImageButton mBackBtn;
    private boolean startScanBle;
    private boolean jumpBanding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_bluetooth);
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        mAllowBlueBtn.setOnClickListener(mOnClickListener);
        mAllowLocationBtn.setOnClickListener(mOnClickListener);
        mBackBtn.setOnClickListener(mOnClickListener);
    }

    private void initView() {
        mAllowLocationTv = findViewById(R.id.allow_location_tv);
        mAllowLocationBtn = findViewById(R.id.allow_location_btn);
        mAllowBlueBtn = findViewById(R.id.allow_bluetooth_btn);
        mAllowBlueTv = findViewById(R.id.allow_bluetooth_tv);
        mBackBtn = findViewById(R.id.back_btn);
    }

    private void initData() {
        if(AndPermission.hasPermission(OpenBluetoothActivity.this, Permission.LOCATION)){
            mAllowLocationTv.setVisibility(View.VISIBLE);
            mAllowLocationBtn.setVisibility(View.GONE);
        }else {
            mAllowLocationTv.setVisibility(View.GONE);
            mAllowLocationBtn.setVisibility(View.VISIBLE);
        }
        if(UbtBluetoothManager.getInstance().isOpenBluetooth()){
            mAllowBlueBtn.setVisibility(View.GONE);
            mAllowBlueTv.setVisibility(View.VISIBLE);
        }else {
            mAllowBlueBtn.setVisibility(View.VISIBLE);
            mAllowBlueTv.setVisibility(View.GONE);
        }
    }

    private PermissionLocationRequest mLocationRequest;
    AnalysisClickListener mOnClickListener = new AnalysisClickListener(){
        @Override
        public void onClick(View view,boolean reported) {
            switch (view.getId()){
                case R.id.back_btn:
                    finish();
                    break;
                case R.id.allow_bluetooth_btn:
                    if (UbtBluetoothManager.getInstance().isOpenBluetooth()){
                        gotoOpenPowerPage();
                    }else {
                        UbtBluetoothManager.getInstance().openBluetooth(OpenBluetoothActivity.this);
                    }
                    break;
                case R.id.allow_location_btn:
                    if (AndPermission.hasPermission(OpenBluetoothActivity.this, Permission.LOCATION)) {
                        gotoOpenPowerPage();
                    }else {
                        if (null == mLocationRequest) {
                            mLocationRequest = new PermissionLocationRequest(OpenBluetoothActivity.this);
                        }
                        mLocationRequest.request(new PermissionLocationRequest.PermissionLocationCallback() {
                            @Override
                            public void onSuccessful() {
                                Log.d(TAG, "开启定位权限成功");
                                mAllowLocationTv.setVisibility(View.VISIBLE);
                                mAllowLocationBtn.setVisibility(View.GONE);
                                gotoOpenPowerPage();
                            }

                            @Override
                            public void onFailure() {
                                Log.d(TAG, "开启定位权限失败");
                                Utils.getAppDetailSettingIntent(OpenBluetoothActivity.this);
                            }

                            @Override
                            public void onRationSetting() {
                                Log.d(TAG, "用户勾选过不再提醒提示");
                                Utils.getAppDetailSettingIntent(OpenBluetoothActivity.this);
                            }
                        });
                    }
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        UbtBluetoothHelper.getInstance().stopScan();
    }


    @Override
    public void onMessageEvent(OpenBluetoothEvent event) {
        super.onMessageEvent(event);
        int blueState = event.mState;
        switch (blueState) {
            case BluetoothAdapter.STATE_TURNING_ON:
                mAllowBlueTv.setVisibility(View.VISIBLE);
                mAllowBlueBtn.setVisibility(View.GONE);
                Log.d(TAG, " STATE_TURNING_ON ");
                if(AndPermission.hasPermission(OpenBluetoothActivity.this, Permission.LOCATION)){
                    Log.d(TAG, " STATE_TURNING_ON 1");
                    UbtBluetoothHelper.getInstance().startScan(MyRobotsLive.getInstance().getRobotUserId());
                    UbtBluetoothHelper.getInstance().setBluetoothScanListener(new UbtBluetoothHelper.BluetoothScanListener() {
                        @Override
                        public void scanEnd() {
                            Log.d(TAG, " isReadyToNextStep ");
                            dismissDialog();
                            jumpBanding();
                        }


                    });
                    showLoadingDialog();
                    startScanBle = true;
                }
//                mHandler.sendEmptyMessageDelayed(OPEN_POWER_PAGE_MSG_WATH,1000);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                mAllowBlueTv.setVisibility(View.GONE);
                mAllowBlueBtn.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

    }

    private void gotoOpenPowerPage(){
        if(!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if(UbtBluetoothManager.getInstance().isOpenBluetooth() && AndPermission.hasPermission(OpenBluetoothActivity.this, Permission.LOCATION)) {
            if(UbtBluetoothHelper.getInstance().scanEnd()) {
                jumpBanding();
            } else {
                showLoadingDialog();
                jumpBanding = true;
            }
        }
    }

    private void jumpBanding() {
        jumpBanding = false;
        if(!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if (UbtBluetoothHelper.getInstance().getPurposeDevice() != null) {
            Intent intent = new Intent(this, BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
        } else if (UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(this);
        } else {
            PageRouter.toOpenPowerTip(this);
        }
        finish();
    }
}
