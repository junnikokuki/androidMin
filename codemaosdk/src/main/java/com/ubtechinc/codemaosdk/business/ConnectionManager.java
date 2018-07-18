package com.ubtechinc.codemaosdk.business;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.IConnectionControl;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.server.HeartBeatStrategy;
import com.google.common.collect.Lists;
import com.ubtech.utilcode.utils.ListUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.bean.UbtBleDevice;
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;
import com.ubtechinc.protocollibrary.protocol.MiniBleProto;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Deseription 连接管理类
 * @Author tanghongyu
 * @Time 2018/3/19 16:20
 */

public class ConnectionManager implements IConnectionControl {
    private static final String TAG = "ConnectManager";
    private static ConnectionManager mInstance;
    private List<WeakReference<IRobotBleControl.ConnectingResultCallback>> iConnectResultCallbackList = Lists.newArrayList();
    AtomicBoolean isConnected = new AtomicBoolean(false);
    private UbtBleDevice mConnectedDevice;
    //是否主动断开
    private boolean isOperationDisConnect;
    private boolean mIsScaning = false;
    private ConnectionManager() {
    }

    public static ConnectionManager get() {

        if (mInstance == null) {
            synchronized (ConnectionManager.class) {
                mInstance = new ConnectionManager();
            }
        }
        return mInstance;
    }

    public void init(Application context) {
        BleManager.getInstance().init(context);
        BleManager.getInstance()
                .enableLog(true)
                .setMaxConnectCount(7)
                .setSplitWriteNum(MiniBleProto.INSTANCE.getSplitLength())
                .setOperateTimeout(5000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(30 * 1000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .setDeviceName(true, MiniBleProto.INSTANCE.getMiniNameFilter())
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }

    public UbtBleDevice getConnectedRobot() {


        return mConnectedDevice;
    }


    /**
     * 扫描周围机器人
     *
     * @param scanResultCallback
     */
    public void scanRobot(BleScanRuleConfig scanRuleConfig, final IRobotBleControl.ScanResultCallback scanResultCallback) {

        if (scanRuleConfig != null) {
            BleManager.getInstance().initScanRule(scanRuleConfig);
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 开始扫描（主线程）
                mIsScaning = success;
                if(scanResultCallback != null) {
                    scanResultCallback.onScanStarted(success);
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 扫描到一个符合扫描规则的BLE设备（主线程）
                if (scanResultCallback != null) {
                    UbtBleDevice ubtBleDevice = new UbtBleDevice();
                    ubtBleDevice.setDevice(bleDevice);
                    try {
                        String deviceName = bleDevice.getName();
                        String[] data = deviceName.split("_");
                        deviceName = data[1];
                        ubtBleDevice.setSn(deviceName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ubtBleDevice.setSn(bleDevice.getName());
                    }

                    scanResultCallback.onScanning(ubtBleDevice);
                }

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）
                if (scanResultCallback != null) {
                    scanResultCallback.onStop();
                }
                mIsScaning = false;

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                Log.d(TAG, "onLeScan device = " + bleDevice.toString());
                super.onLeScan(bleDevice);
            }
        });
    }


    public void stopScanRobot() {
        if(mIsScaning) {
            BleManager.getInstance().cancelScan();
        }

    }

    public void registerRobotStateListener(IRobotBleControl.ConnectingResultCallback connectStateCallback) {

        if(connectStateCallback == null) return;
        if(ListUtils.isEmpty(iConnectResultCallbackList)) {
            WeakReference newWeakRef = new WeakReference(connectStateCallback);
            this.iConnectResultCallbackList.add(newWeakRef);
        }else {

            for(int i=0; i<iConnectResultCallbackList.size(); i++ ) {
                WeakReference weakReference  = iConnectResultCallbackList.get(i);
                if(connectStateCallback != null && weakReference.get() == connectStateCallback) {
                    LogUtils.w("registerRobotBleStateListener fail dupalication ! ");
                    return;
                }else {
                    WeakReference newWeakRef = new WeakReference(connectStateCallback);
                    this.iConnectResultCallbackList.add(newWeakRef);
                    LogUtils.d("registerRobotBleStateListener success");
                }
            }

        }


    }

    public void  unregisterRobotStateListener(IRobotBleControl.ConnectingResultCallback connectStateCallback) {
        for(int i=0; i<iConnectResultCallbackList.size(); i++ ) {
            WeakReference weakReference  = iConnectResultCallbackList.get(i);
            if(connectStateCallback != null && weakReference.get() == connectStateCallback) {
                iConnectResultCallbackList.remove(i);
                LogUtils.d("unregisterRobotStateListener ");
            }
        }
    }

    public void connectRobot(UbtBleDevice bleDevice) {
        BleManager.getInstance().cancelScan();
        if(mConnectedDevice != null)BleManager.getInstance().disconnect(mConnectedDevice.getDevice());
        LogUtils.d("connectRobot = " + bleDevice.getSn());
        BleManager.getInstance().connect(bleDevice.getDevice(), bleGattCallback);

    }


    BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {

        }

        @Override
        public void onConnectFail(BleException exception) {

            LogUtils.w("onConnectFail e = " + exception.getDescription());
            // 连接失败
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {
                    callbackConnectFail();
                }
            });

        }

        @Override
        public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
            // 连接成功，BleDevice即为所连接的BLE设备

            final UbtBleDevice ubtBleDevice = new UbtBleDevice();
            ubtBleDevice.setDevice(bleDevice);
            try {
                String deviceName = bleDevice.getName();
                String[] data = deviceName.split("_");
                deviceName = data[1];
                ubtBleDevice.setSn(deviceName);
            } catch (Exception e) {
                e.printStackTrace();
                ubtBleDevice.setSn(bleDevice.getName());
            }
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {


                    mConnectedDevice = ubtBleDevice;
                    Phone2RobotMsgMgr.get().init(ConnectionManager.this);
                    isConnected.set(true);
                    LogUtils.d("onConnectSuccess ");
                    HeartBeatStrategy.Companion.getInstance().startHeartBeat();
                    callbackConnected(ubtBleDevice);
                }
            });


        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

            BleManager.getInstance().destroy();
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {
                    // 连接中断，isActiveDisConnected表示是否是主动调用了断开连接方法
                    callbackDisconnected();

                    isConnected.set(false);
                    if (isOperationDisConnect) {
                        mConnectedDevice = null;
                    }
                    LogUtils.w("onDisConnected ");
                    HeartBeatStrategy.Companion.getInstance().stopHeartBeat();
                }
            });


        }
    };

    public boolean isConnected() {
        return isConnected.get();
    }

    public void disconnectRobot() {
        isOperationDisConnect = true;
        BleManager.getInstance().disconnectAllDevice();
    }

    private void callbackDisconnected() {
        for (WeakReference<IRobotBleControl.ConnectingResultCallback> data: iConnectResultCallbackList ) {
            if(data.get() != null) {
                data.get().onDisconnected();
            }
        }
    }
    private void callbackConnected(UbtBleDevice ubtBleDevice) {
        for (WeakReference<IRobotBleControl.ConnectingResultCallback> data: iConnectResultCallbackList ) {
            if(data.get() != null) {
                data.get().onConnected(ubtBleDevice);
            }
        }
    }

    private void callbackConnectFail() {
        for (WeakReference<IRobotBleControl.ConnectingResultCallback> data: iConnectResultCallbackList ) {
            if(data.get() != null) {
                data.get().onConnectFail();
            }
        }
    }

    @Override
    public void disconnect() {
        BleManager.getInstance().disconnectAllDevice();
    }


    @Override
    public void sendHeartBeat(final @NotNull IHeartCallback iHeartCallback) {
            Phone2RobotMsgMgr.get().sendData(CmdId.BL_HEART_BEAT_REQUEST, CmdId.IM_VERSION, null, "", new ICallback() {
                @Override
                public void onSuccess(Object data) {
                    iHeartCallback.onSuccess();
                }

                @Override
                public void onError(ThrowableWrapper e) {
                    iHeartCallback.onFail();
                }
            });
    }
}
