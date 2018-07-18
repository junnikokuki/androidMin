package com.ubtechinc.alpha.mini.common;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.BangdingManager;
import com.ubtechinc.alpha.mini.ui.bluetooth.ShowErrorDialogUtil;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.OpenBluetoothEvent;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author：wululin
 * @date：2017/11/1 20:08
 * @modifier：ubt
 * @modify_date：2017/11/1 20:08
 * [A brief description]
 * version
 */

public abstract class BleNetworkBaseActivity extends BaseActivity {

    private MaterialDialog mMaterialDialog;
    private MaterialDialog mBleDisconnectDialog;

    protected BangdingManager mBangdingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenBluetoothEvent event) {
        int blueState = event.mState;
        switch (blueState) {
            case BluetoothAdapter.STATE_ON:
                if (mMaterialDialog != null) {
                    mMaterialDialog.dismiss();
                }
//                UbtBluetoothDevice device = UbtBluetoothManager.getInstance().getCurrentDevices();
//                UbtBluetoothManager.getInstance().closeConnectBle();
//                if (device != null) {
//                    UbtBluetoothManager.getInstance().connectBluetooth(device);
//                }
                break;

            case BluetoothAdapter.STATE_OFF:
                showBletoothOffDialog();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 蓝牙连接中断对话框
     */
    protected void showBleDisconnectDialog(){

        if(mBleDisconnectDialog == null){
            mBleDisconnectDialog = new MaterialDialog(this);
        }
        mBleDisconnectDialog.setTitle(R.string.ble_disconnect)
                .setMessage(R.string.ble_connect_try_again)
                .setNegativeButton(R.string.exit_ble, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBleDisconnectDialog.dismiss();
                        if(UbtBluetoothManager.getInstance().isChangeWifi()) {
                            PageRouter.toAlphaInfoActivityTop(BleNetworkBaseActivity.this);
                        } else {
                            PageRouter.toBangdingActivity(BleNetworkBaseActivity.this);
                            ShowErrorDialogUtil.finishAllWifiAcitivity();
                        }
                    }
                }).setPositiveButton(R.string.please_try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBleDisconnectDialog.dismiss();
                UbtBluetoothDevice device = UbtBluetoothManager.getInstance().getCurrentDevices();
                if(device != null){
                    onReconnectDevice(device);
                    UbtBluetoothManager.getInstance().connectBluetooth(device);
                }
            }
        }).show();
    }

    public abstract void onReconnectDevice(UbtBluetoothDevice device);
    /**
     * 蓝牙关闭对话框
     */
    private void showBletoothOffDialog() {
        if (mMaterialDialog == null) {
            mMaterialDialog = new MaterialDialog(this);
        }
        if (mMaterialDialog.isShowing()) {
            return;
        }
        mMaterialDialog.setTitle(R.string.bletooth_off_title).setMessage(R.string.please_open_ble)
                .setPositiveButton(R.string.open_ble, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMaterialDialog.dismiss();
                        PageRouter.toOpenBluetooth(BleNetworkBaseActivity.this);
                        ShowErrorDialogUtil.finishAllWifiAcitivity();
//                        UbtBluetoothManager.getInstance().openBluetooth();
                    }
                }).setNegativeButton(R.string.exit_ble, new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
                PageRouter.toMain(BleNetworkBaseActivity.this);
                ShowErrorDialogUtil.finishAllWifiAcitivity();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap)
                .setCanceledOnTouchOutside(false).show();
    }

}
