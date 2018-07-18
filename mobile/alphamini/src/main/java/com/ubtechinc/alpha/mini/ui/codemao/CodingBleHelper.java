package com.ubtechinc.alpha.mini.ui.codemao;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.clj.fastble.scan.BleScanRuleConfig;
import com.ubtech.utilcode.utils.ListUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.constants.BusinessConstants;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.bluetooth.utils.BleUtil;
import com.ubtechinc.codemaosdk.MiniApi;
import com.ubtechinc.codemaosdk.bean.UbtBleDevice;
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl;
import com.ubtechinc.protocollibrary.protocol.MiniBleProto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desc : 蓝牙助手类
 * @author: tanghongyu
 * @email : hongyu.tang@ubtrobot.com
 * @time : 2018/5/30
 */

public class CodingBleHelper {

    private static final int MSG_ONE_DEVICE_SCAN_DELAY = 1;
    private static final int MSG_SCAN_DELEY = 2;
    private static final double AUTOCONNECT_DISTANCE = 1.0;
    public static final double AUTOCONNECT_MIDDLE_DISTANCE = 2.5;
    public static final String KEY_UBTBLUETOOTH = "key_ubtbluetooth";
    private static final String TAG = "CodingBleHelper";
    //  搜索第一个设备时间
    private static final int ONE_DEVICE_SCAN_DELAY = 1500;
    // 搜索总时间
    private static final int SCAN_DELAY = ONE_DEVICE_SCAN_DELAY + 500;
    private static final int SEARCH_TIMEOUT = 30 * 1000;
    private List<String> mFilterDevices = new ArrayList<>();
    private Map<String, UbtBleDevice> mScanedDevices = new HashMap<>();

    private long scanTime;
    private volatile boolean mIsStopScan = false;
    private volatile static CodingBleHelper instance;
    private BluetoothScanListener bluetoothScanListener;
    private boolean mIsStartScan;
    private Handler mHandler;
    private boolean onDeviceCompletely;
    private boolean scanCompletely;
    private int tryConnectCount = 3;
    // 指定搜索时间
    private int searchTime = 0;

    private CodingBleHelper() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, " what : " + msg.what);
                switch (msg.what) {
                    case MSG_ONE_DEVICE_SCAN_DELAY:
                        onDeviceCompletely = true;
                        if (isReadyToNextStep()) {
                            notifyScanEnd();
                        } else {
                            Log.d(TAG, " sendEmptyMessageDelayed what : " + MSG_SCAN_DELEY);
                            mHandler.sendEmptyMessageDelayed(MSG_SCAN_DELEY, SCAN_DELAY - ONE_DEVICE_SCAN_DELAY);
                        }
                        break;
                    case MSG_SCAN_DELEY:
                        scanCompletely = true;
                        notifyScanEnd();
                        break;
                }
            }
        };
    }

    private void notifyScanEnd() {
        if (bluetoothScanListener != null) {
            LogUtils.d("notifyScanEnd");
            bluetoothScanListener.readyToJump();
        }
    }

    public static CodingBleHelper getInstance() {
        if (instance == null) {
            synchronized (CodingBleHelper.class) {
                if (instance == null) {
                    instance = new CodingBleHelper();
                }
            }
        }
        return instance;
    }

    public void setBluetoothScanListener(BluetoothScanListener bluetoothScanListener) {
        this.bluetoothScanListener = bluetoothScanListener;
    }

    public void scanAndConnect() {

    }



    /**
     * @param type
     * @param robotInfos
     */
    public void startScan(int type, List<RobotInfo> robotInfos) {
        if (!ListUtils.isEmpty(robotInfos)) {
            for (RobotInfo robotInfo : robotInfos) {
                mFilterDevices.add(robotInfo.getRobotUserId());
            }
        }
        mScanedDevices.clear();
        mIsStartScan = true;
        onDeviceCompletely = false;
        scanCompletely = false;
        mIsStopScan = false;
        String nameFilter = "";

        switch (type) {
            case BusinessConstants.SCAN_TYPE_CODEMAO:
                nameFilter = MiniBleProto.INSTANCE.getCodingNameFilter();
                break;
            case BusinessConstants.SCAN_TYPE_MINI:
                nameFilter = MiniBleProto.INSTANCE.getMiniNameFilter();
                break;

        }
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(30 * 1000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .setDeviceName(true, nameFilter)
                .build();
        MiniApi.get().startScanRobot(scanRuleConfig,scanResultCallback);
        scanTime = System.currentTimeMillis();
        mHandler.removeMessages(MSG_ONE_DEVICE_SCAN_DELAY);
        mHandler.sendEmptyMessageDelayed(MSG_ONE_DEVICE_SCAN_DELAY, ONE_DEVICE_SCAN_DELAY);

    }


    IRobotBleControl.ScanResultCallback scanResultCallback =  new IRobotBleControl.ScanResultCallback() {
        @Override
        public void onScanStarted(boolean b) {

        }

        @Override
        public void onScanning(UbtBleDevice device) {
            Log.d(TAG, " scanSuccess -- device: " + device.getSn() + " mac = " + device.getDevice().getMac());
            String robotSn = device.getSn();
            if (ListUtils.isEmpty(mFilterDevices)) {//没有过滤，则搜索所有设备
                if (mScanedDevices.containsKey(robotSn)) {
                    mScanedDevices.remove(robotSn);
                }
                mScanedDevices.put(robotSn, device);

            } else if (mFilterDevices.contains(robotSn)) {//过滤设备
                if (mScanedDevices.containsKey(robotSn)) {
                    mScanedDevices.remove(robotSn);
                }
                mScanedDevices.put(robotSn, device);
            }

        }


        @Override
        public void onStop() {
            Log.d(TAG, " scanSuccess -- scanTimeOut: ");
            if (!mIsStopScan && --tryConnectCount > 0) {
                mScanedDevices.clear();
                MiniApi.get().startScanRobot(this);
                scanTime = System.currentTimeMillis();
            }

        }
    };
    /**
     * 是否允许下一步操作,两种情况未结束，1、1.5s内 2、1.5s内仅有一个设备时表示未结束
     *
     * @return
     */
    public boolean isReadyToNextStep() {
        // 未开始直接返回true
        Log.d(TAG, " isReadyToNextStep mIsStartScan : " + mIsStartScan + " onDeviceCompletely : " + onDeviceCompletely + " mScanedDevices.size() : " + mScanedDevices.size() + " scanCompletely = " + scanCompletely);
        if (!mIsStartScan) {
            return true;
        }
        if (!onDeviceCompletely) {
            Log.d(TAG, " isReadyToNextStep -- 1 ");
            return false;
        } else if (onDeviceCompletely && mScanedDevices.size() == 1 && !scanCompletely) {
            Log.d(TAG, " isReadyToNextStep -- 2 ");
            return false;
        } else {
            Log.d(TAG, " isReadyToNextStep -- 3 ");
            return true;
        }
    }

    public boolean hasDevices() {
        return !mScanedDevices.isEmpty();
    }

    /**
     * 获取范围内目标设备,如果只有一个，则直接返回。 如果是多个，只有一个达到近距离要求的，返回，如果多个都是近距离，则返回空。
     *
     * @return 目标设备
     */
    public UbtBleDevice getPurposeDevice() {
        return getPurposeDevice(AUTOCONNECT_DISTANCE);
    }

    public UbtBleDevice getPurposeDevice(double distance) {

        UbtBleDevice ubtBluetoothDevice = null;
        Log.d(TAG, " mScanedDevices.size() : " + mScanedDevices.size());
        double minDistance = distance;
        if (isReadyToNextStep() && mScanedDevices.size() != 0) {
            boolean isOnly = (mScanedDevices.size() == 1);
            boolean rssiOnly = true;
            Collection<UbtBleDevice> bleDevices = mScanedDevices.values();
            for (UbtBleDevice bleDevice : bleDevices) {
                if (isOnly) {
                    return bleDevice;
                }
                double rssiDistance = BleUtil.getDistance(bleDevice.getDevice().getRssi());
                Log.i(TAG, " sn : " + bleDevice.getSn() + " distance = " + rssiDistance);
                if (rssiDistance <= minDistance) {
                    Log.d(TAG, " rssi : " + bleDevice.getDevice().getRssi());
                    if (rssiOnly && ubtBluetoothDevice == null) {
                        ubtBluetoothDevice = bleDevice;
                    } else if (ubtBluetoothDevice != null) {
                        rssiOnly = false;
                        ubtBluetoothDevice = null;
                    }
                }
            }
        }
        Log.d(TAG, " ubtBluetoothDevice " + ubtBluetoothDevice);
        return ubtBluetoothDevice;
    }

    private long getIntervalTime() {
        return System.currentTimeMillis() - scanTime;
    }

    public void stopScan() {
        Log.d(TAG, "stopScan");
        mHandler.removeMessages(MSG_ONE_DEVICE_SCAN_DELAY);
        mHandler.removeMessages(MSG_SCAN_DELEY);
        bluetoothScanListener = null;
        if (mIsStartScan) {
            mIsStartScan = false;
            mIsStopScan = true;
            MiniApi.get().stopScanRobot();
        }
    }

    public interface BluetoothScanListener {
        void readyToJump();
    }
}
