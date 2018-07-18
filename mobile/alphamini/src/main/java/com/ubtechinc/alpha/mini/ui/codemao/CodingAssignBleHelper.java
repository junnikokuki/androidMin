package com.ubtechinc.alpha.mini.ui.codemao;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.clj.fastble.scan.BleScanRuleConfig;
import com.ubtech.utilcode.utils.ListUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.StringUtils;
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

public class CodingAssignBleHelper {

    private static final int MSG_ONE_DEVICE_SCAN_DELAY = 1;
    private static final int MSG_SCAN_DELEY = 2;
    private static final double AUTOCONNECT_DISTANCE = 1.0;
    public static final String KEY_UBTBLUETOOTH = "key_ubtbluetooth";
    //  搜索第一个设备时间
    private static final int ONE_DEVICE_SCAN_DELAY = 1500;
    // 搜索总时间
    private static final int SCAN_DELAY = ONE_DEVICE_SCAN_DELAY + 500;
    private static final int SEARCH_TIMEOUT = 30 * 1000;
    private List<String> mFilterDevices = new ArrayList<>();
    private Map<String, UbtBleDevice> mScanedDevices = new HashMap<>();

    private long scanTime;
    private volatile boolean mIsStopScan = false;
    private volatile static CodingAssignBleHelper instance;
    private BluetoothScanListener bluetoothScanListener;
    private boolean mIsStartScan;
    private Handler mHandler;

    private CodingAssignBleHelper() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                LogUtils.d(" what : " + msg.what);
                switch (msg.what) {
                    case MSG_ONE_DEVICE_SCAN_DELAY:

                        notifyScanEnd();
                        stopScan();
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

    public static CodingAssignBleHelper getInstance() {
        if (instance == null) {
            synchronized (CodingAssignBleHelper.class) {
                if (instance == null) {
                    instance = new CodingAssignBleHelper();
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

    public void startScanAssignRobot(int type, UbtBleDevice ubtBleDevice) {
        LogUtils.d("startScanAssignRobot type = " + type + " device = " + ubtBleDevice.getSn());
        if (ubtBleDevice != null) {
            mFilterDevices.add(ubtBleDevice.getSn());
        }
        mScanedDevices.clear();
        mIsStartScan = true;
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
                .setScanTimeOut(5 * 1000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .setDeviceName(true, nameFilter)
                .build();

        MiniApi.get().startScanRobot(scanRuleConfig, new IRobotBleControl.ScanResultCallback() {
            @Override
            public void onScanStarted(boolean b) {

            }

            @Override
            public void onScanning(UbtBleDevice device) {
                LogUtils.d(" scan -- device: " + device.getSn() + " mac = " + device.getDevice().getMac());
                String robotSn = device.getSn();
                if (ListUtils.isEmpty(mFilterDevices)) {//没有过滤，则搜索所有设备
                    if (mScanedDevices.containsKey(robotSn)) {
                        mScanedDevices.remove(robotSn);
                    }
                    mScanedDevices.put(robotSn, device);

                } else if (isFilterConstanins(robotSn)) {//过滤设备
                    if (mScanedDevices.containsKey(robotSn)) {
                        mScanedDevices.remove(robotSn);
                    }

                    mScanedDevices.put(robotSn, device);
                    LogUtils.d("find assign device = " + robotSn);
                    mHandler.sendEmptyMessage(MSG_ONE_DEVICE_SCAN_DELAY);

                }

            }


            @Override
            public void onStop() {
                LogUtils.d(" scanTimeOut: ");
                if (!mIsStopScan) {
                    mScanedDevices.clear();
                    notifyScanEnd();
                    scanTime = System.currentTimeMillis();
                }

            }
        });
        scanTime = System.currentTimeMillis();

    }
    private boolean isFilterConstanins(String sn) {
        for(String filter : mFilterDevices) {
            if(StringUtils.isEquals(filter, sn)) {
                return true;
            }
        }
        return false;
    }
    public UbtBleDevice getAssignRobot() {

        Collection<UbtBleDevice> bleDevices = mScanedDevices.values();

        try {
            LogUtils.d("getAssignRobot = " + bleDevices.iterator().next());
            return bleDevices.iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean hasDevices() {
        return !mScanedDevices.isEmpty();
    }


    private long getIntervalTime() {
        return System.currentTimeMillis() - scanTime;
    }

    public void stopScan() {
        LogUtils.d("stopScan");
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
