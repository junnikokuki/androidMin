package com.clj.fastble.server;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;


import com.clj.fastble.data.DefaultDataEngine;
import com.clj.fastble.data.IDataEngine;
import com.clj.fastble.utils.BleUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.clj.fastble.utils.BleUtil.getUUID;


/**
 * @author：wululin
 * @date：2017/10/19 15:48
 * @modifier：ubt
 * @modify_date：2017/10/19 15:48
 * [A brief description]
 * 蓝牙连接管理类
 */
public class UbtBluetoothConnManager {

    private static final String TAG = UbtBluetoothConnManager.class.getSimpleName();
    private static final String BLUETOOTH_PROTOCOL_VERSION="V2.0";
    //连接成功
    private static final int CONN_SUCCESS_MSG_WATH = 0x001;
    //连接失败
    private static final int CONN_FIALD_MSG_WATH = 0x002;
    //断开
    private static final int DISCONNECT_MSG_WATH = 0x003;
    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattServer mBlueGattServer;
    private UBTGattService mGattService;
    private BluetoothLeAdvertiser mAdvertiser;
    private String mBleNamePrefix;
    private String mSerialNumber;
    private AdvertiseSettings mAdvSettings;
    private AdvertiseData mAdvData;
    private AdvertiseData mAdvScanResponse;
    private IBluetoothDataCallback mDataCallback;
    private BleConnectListener mBluetoothConnCallback;
    private volatile BluetoothDevice mCurrentDevice;
    private ExecutorService mThreadExecutor;
    private IDataEngine iDataEngine;
    // 判断是否写入成功
    private volatile boolean wirteSuc;
    private Lock mLock = new ReentrantLock();
    private Condition condition = mLock.newCondition();

    private Handler mHandler  = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONN_SUCCESS_MSG_WATH:
                    if(mBluetoothConnCallback!= null){
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        mBluetoothConnCallback.onSuccess(device);
                    }
                    break;

                case CONN_FIALD_MSG_WATH:
                    if(mBluetoothConnCallback != null){
                        mBluetoothConnCallback.onFailed();
                    }
                    break;

                case DISCONNECT_MSG_WATH:
                    if(mBluetoothConnCallback != null){
                        mBluetoothConnCallback.onLost();
                    }
                    break;
            }
        }
    };
    private static class UbtBluetoothManagerHolder {
        public static UbtBluetoothConnManager instance = new UbtBluetoothConnManager();
    }
    private UbtBluetoothConnManager(){
    }

    public static UbtBluetoothConnManager getInstance(){
        return UbtBluetoothManagerHolder.instance;
    }

    public void init(Context context,String bleNamePrefix,String serialNumber){
        this.mContext = context;
        this.mBleNamePrefix = bleNamePrefix;
        this.mSerialNumber = serialNumber;
        mBluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mThreadExecutor =  Executors.newCachedThreadPool();
        initBluetoothServer();
        iDataEngine = new DefaultDataEngine();
    }
    public void initDataHandleEngine(IDataEngine iDataEngine) {
        this.iDataEngine = iDataEngine;
    }
    /**
     * 初始化蓝牙
     */
    private void initBluetoothServer() {
        mBlueGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
        if(mBlueGattServer != null) {
            mGattService = new UBTGattService();
            mBlueGattServer.addService(mGattService.getGattService());
            initAdvertiser();
        }else{
            Log.e(TAG, "初始化蓝牙设备失败!!!");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
//                    initBluetoothServer();
                }
            },2000);
        }

    }

    /**
     * 初始化蓝牙广播信息
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initAdvertiser() {
        mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        StringBuffer buffer = new StringBuffer();
        buffer.append(mBleNamePrefix).append(mSerialNumber);
        boolean isSettingOK = mBluetoothAdapter.setName(buffer.toString());
        Log.d(TAG, "bluetooth name change is :" + isSettingOK);
        mAdvSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)//设置关闭的强弱和延时
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)//设置广播发送的频率等级
                .setConnectable(true) //设置广播是连接还是不连接
                .build();
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getUUID()));
        mAdvData = new AdvertiseData.Builder()
                .addServiceUuid(pUuid)
                .addServiceData(pUuid,BLUETOOTH_PROTOCOL_VERSION.getBytes())
                .build();

        mAdvScanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true) //设置设备的名字是否要在广播的packet
                .build();

        if (mAdvertiser != null){
            mAdvertiser.startAdvertising(mAdvSettings, mAdvData, mAdvScanResponse, mAdvCallback);
        }else{
            Log.e(TAG, "初始化蓝牙广播失败!!!!!!!!!!");
        }
        registerBleReceiver();
    }

    private void registerBleReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBleReceiver,intentFilter);
    }

    public void unRegisterBleReceiver() {
        mContext.unregisterReceiver(mBleReceiver);
    }


    public void setDataCallback(IBluetoothDataCallback dataCallback){
        this.mDataCallback = dataCallback;
    }

    public void setBluetoothConnCallback(BleConnectListener bluetoothConnCallback){
        this.mBluetoothConnCallback = bluetoothConnCallback;
    }



    /**
     * 通过蓝牙发送数据
     * @param data 蓝牙数据
     */
    public void sendBleData(byte[] data){
        Log.d(TAG,"data=====: "+bytes2HexString(data));
        SendBluetoothData sendBluetoothData = new SendBluetoothData(data,mCurrentDevice);
        mThreadExecutor.execute(sendBluetoothData);
    }
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * 发送蓝牙数据的任务
     */
    class SendBluetoothData implements Runnable{
        private byte[] data;
        private BluetoothDevice mBluetoothDevice;
        public SendBluetoothData(byte[] data,BluetoothDevice bluetoothDevice){
            this.data = data;
            this.mBluetoothDevice = bluetoothDevice;
        }

        @Override
        public void run() {
            BluetoothGattCharacteristic characteristic = mGattService.getHeartWriteCharacteristic();

            List<byte[]> packets = splitByte(data, 20);
            synchronized (UbtBluetoothConnManager.class) {
                for (int i = 0; i < packets.size(); i++) {
                    byte[] bytes = packets.get(i);
                    if(bytes != null){
                        if(mBlueGattServer != null && mBluetoothDevice != null){
                            characteristic.setValue(bytes);
                            mBlueGattServer.notifyCharacteristicChanged(mBluetoothDevice, characteristic, false);
                            SystemClock.sleep(20);
                        }
                    }
                }

            }
        }
    }

    private  List<byte[]> splitByte(byte[] data, int count) {
        return iDataEngine.spliteData(data);
    }
    AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG,"Advertising onStartSuccess: ");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.i(TAG,"Advertising onStartFailure: " + errorCode);
//            initAdvertiser();
            BleUtil.closeBle();
        }

    };

    private BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG,"blueState=====" + blueState);
                switch (blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        initAdvertiser();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        BleUtil.openBle();
                        break;
                }
                mContext.unregisterReceiver(mBleReceiver);
            }
        }
    };


    public boolean isConnected() {
        return mCurrentDevice != null;
    }
    public BluetoothDevice getCurrentDevice(){
        return mCurrentDevice;
    }
    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG,"status====" + status + ";;newState=====" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS){
                if (newState == BluetoothGatt.STATE_CONNECTED){
                    if(mCurrentDevice == null){
                        mCurrentDevice = device;
                        Message msg = new Message();
                        msg.what = CONN_SUCCESS_MSG_WATH;
                        msg.obj = device;
                        mHandler.sendMessage(msg);
                    }else {
                        mBlueGattServer.cancelConnection(device);
                    }
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED){
                    if(mCurrentDevice != null){
                        if(mCurrentDevice.getAddress().equals(device.getAddress())){
//                        mBlueGattServer.cancelConnection(mCurrentDevice);
                            mCurrentDevice = null;
                            mHandler.sendEmptyMessage(DISCONNECT_MSG_WATH);

                        }
                    }
                }
            } else{
                mHandler.sendEmptyMessage(CONN_FIALD_MSG_WATH);
                Log.e(TAG, "设备连接失败==" + status);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
            Log.i(TAG, "read Value: " + Arrays.toString(characteristic.getValue())+"  offset="+offset);
            String wiwiListString = new String();
            byte b[] = wiwiListString.getBytes();//String转换为byte[]
            //这里是读请求，可以把WIFI列表返回给手机
            if (offset != 0) {
                mBlueGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                        b);
                return;
            }
            mBlueGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                    offset, b);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.i(TAG, "onNotificationSent status " + status);
            if(status == 0) {

            }
            super.onNotificationSent(device, status);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);
            Log.i(TAG, "Characteristic Write request: " + Arrays.toString(value) +
                    "  offset=" + offset + ";;responseNeeded===" + responseNeeded + ";;requestId====" + requestId + ";;device====" + device);
            if(responseNeeded){
                if(!isDisconnect){
                    mBlueGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,null);
                    Log.i(TAG, " sendResponse BluetoothGatt.GATT_SUCCESS ");
                }
                isDisconnect = false;
            }

            mDataCallback.onReceiveData(value);

        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
                                             int offset,
                                             byte[] value) {
            Log.i(TAG, "Descriptor Write Request " + descriptor.getUuid() + " " + Arrays.toString(value));
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded,
                    offset, value);
            Log.i(TAG, "responseNeeded====" + responseNeeded);
            if(responseNeeded) {
                String s = "hellow world";
                byte b[] = s.getBytes();//String转换为byte[]
                mBlueGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,null);
            }

        }


        //.特征被读取。当回复响应成功后，客户端会读取然后触发本方法
        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.e(TAG, String.format("onDescriptorReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("onDescriptorReadRequest：requestId = %s", requestId));
            mBlueGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }

    };

    private boolean isDisconnect = false;
    public void disconnectCurrentDevevices(){
        if(mCurrentDevice != null){
            isDisconnect = true;
            mBlueGattServer.cancelConnection(mCurrentDevice);
            mCurrentDevice = null;

        }
    }

    public interface BleConnectListener {
        void onSuccess(BluetoothDevice device);
        void onFailed();
        void onLost();
    }

    public interface IBluetoothDataCallback {
        public void onReceiveData(byte[] data);
    }

}
