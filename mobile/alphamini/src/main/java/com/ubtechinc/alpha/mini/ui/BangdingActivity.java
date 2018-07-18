package com.ubtechinc.alpha.mini.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

/**
 * @author：wululin
 * @date：2017/10/26 17:14
 * @modifier：ubt
 * @modify_date：2017/10/26 17:14
 * [A brief description]
 * version
 */

public class BangdingActivity extends BaseActivity {

    private Button mStartBangdingBtn;
    private TextView mEnterMainTv;
    private boolean jumpBanding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangding);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始搜索机器人
        if(UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                AndPermission.hasPermission(this, Permission.LOCATION)){
            UbtBluetoothHelper.getInstance().startScan(MyRobotsLive.getInstance().getRobotUserId());
            UbtBluetoothHelper.getInstance().setBluetoothScanListener(new UbtBluetoothHelper.BluetoothScanListener() {
                @Override
                public void scanEnd() {
                    if(jumpBanding) {
                        dismissDialog();
                        jumpBanding();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UbtBluetoothHelper.getInstance().stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        mStartBangdingBtn = findViewById(R.id.start_bangding);
        mEnterMainTv = findViewById(R.id.enter_main);
    }

    public void initListener(){
        mStartBangdingBtn.setOnClickListener(mClickListener);
        mEnterMainTv.setOnClickListener(mClickListener);
    }

    AnalysisClickListener mClickListener = new AnalysisClickListener(){

        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()){
                case R.id.start_bangding:
                    if(UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                            AndPermission.hasPermission(BangdingActivity.this, Permission.LOCATION)){
                        if(UbtBluetoothHelper.getInstance().scanEnd()) {
                            jumpBanding();
                        } else {
                            jumpBanding = true;
                            showLoadingDialog();
                        }
                    }else {
                        PageRouter.toOpenBluetooth(BangdingActivity.this);
                    }
                    break;
                case R.id.enter_main:
                    PageRouter.toMain(BangdingActivity.this);
                    finish();
                    break;
            }
        }
    };

    private void jumpBanding() {
        jumpBanding = false;
        if(!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(getString(R.string.network_invalid_tip));
            return;
        }
        if (UbtBluetoothHelper.getInstance().getPurposeDevice() != null) {
            Intent intent = new Intent(BangdingActivity.this, BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
            finish();
        } else if (UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(BangdingActivity.this);
            finish();
        } else {
            PageRouter.toOpenPowerTip(BangdingActivity.this);
            finish();
        }
    }
}
