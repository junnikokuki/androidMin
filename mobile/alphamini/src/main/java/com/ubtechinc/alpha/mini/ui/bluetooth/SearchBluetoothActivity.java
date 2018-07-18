package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.BindingRobotSuccessEvent;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.adapter.MiniDeviceAdapter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.BandingListenerAbster;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.CannotSearchAlphaMiniDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.event.BleScanFinishedEvent;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;
import com.ubtechinc.bluetooth.event.BleScanFailEvent;
import com.ubtechinc.bluetooth.event.BleScanResultEvent;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_TIME_OUT_ERROR_CODE;


/**
 * @author：wululin
 * @date：2017/10/13 16:08
 * @modifier：ubt
 * @modify_date：2017/10/13 16:08
 * [A brief description]
 * 蓝牙搜索界面
 */

public class SearchBluetoothActivity extends BleNetworkBaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = SearchBluetoothActivity.class.getSimpleName();
    private static final int MSG_WATH_DISCONNECT_SUCCESS = 0x001;
    /**
     * 扫描到的机器人列表
     */
    private List<UbtBluetoothDevice> robotScanResultList = new ArrayList<UbtBluetoothDevice>();
    private MiniDeviceAdapter mMiniDeviceAdapter;
    private ImageView mBackIv;
    private LinearLayout mSearchWithRobotRl;
    private LinearLayout mSearchingRobotLl;
    private RelativeLayout mSearchTimeOutRl;
    private ListView mResultLv;
    private UbtBluetoothDevice mBluetoothDevice;
    private TextView tvHelper, tvReSearch, tvCannotFind;
    private boolean isConnecting = false;
    private String connectSN; //正在连接的机器人序列号
    private MaterialDialog mByOtherBandingDialog;
    private ProgressBar mPbmoredevice;
    private UbtBluetoothDevice ubtBluetoothDevice;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WATH_DISCONNECT_SUCCESS:
                    mPbmoredevice.setVisibility(View.GONE);
                    UbtBluetoothManager.getInstance().connectBluetooth(mBluetoothDevice);
                    break;
            }
        }
    };
    private enum ScanState {
        SCANNING, SCANNING_WITH_ROBOT, SCANINIG_STOPSCAN, CONNECT
    }
    private ScanState mState = ScanState.SCANNING;
    private void refreshSearchUI() {
        mSearchWithRobotRl.setVisibility(View.GONE);
        mSearchTimeOutRl.setVisibility(View.GONE);
        mSearchingRobotLl.setVisibility(View.GONE);
        if (mState == ScanState.SCANNING) {//处于扫描状态
            mSearchingRobotLl.setVisibility(View.VISIBLE);
        } else if (mState == ScanState.SCANNING_WITH_ROBOT) {//处于扫描搜索到机器人
            mSearchWithRobotRl.setVisibility(View.VISIBLE);
        } else if (mState == ScanState.SCANINIG_STOPSCAN) {//扫描超时，停止扫描
            if (robotScanResultList.size() > 0) {
                mSearchWithRobotRl.setVisibility(View.VISIBLE);
            } else {
                mSearchTimeOutRl.setVisibility(View.VISIBLE);
            }
        } else if (mState == ScanState.CONNECT) {//连接状态
            mSearchWithRobotRl.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bluetooth);
        initView();

        mState = ScanState.SCANNING;
        UbtBluetoothManager.getInstance().setAutoConnect(false);
        startScanBluetooth();
        mMiniDeviceAdapter = new MiniDeviceAdapter(this, robotScanResultList);
        mResultLv.setAdapter(mMiniDeviceAdapter);
        mBangdingManager = new BangdingManager(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanResult(BleScanResultEvent event) {
        Log.d(TAG, " scanSuccess -- device: " + event.device);
        if(mState == ScanState.CONNECT){
            return;
        }
        final UbtBluetoothDevice device = event.device;
        boolean isHas = false;
        if (UbtBluetoothManager.getInstance().isChangeWifi()){
            String currentRobotSn = MyRobotsLive.getInstance().getRobotUserId();
            String scanDeviceSn = device.getSn();

            if (currentRobotSn.equalsIgnoreCase(scanDeviceSn) && robotScanResultList.size() == 0){
                LogUtils.i("receivce add  scanDeviceSn = " + scanDeviceSn);
                robotScanResultList.add(device);
                mMiniDeviceAdapter.onNotifyDataSetChanged(robotScanResultList);
                mState = ScanState.SCANNING_WITH_ROBOT;
                refreshSearchUI();
            }

        }else{
            //当没有绑定机器人或者绑定了机器人，并且序列号相等的情况下，才能加入列表中
            for (UbtBluetoothDevice scanResult : robotScanResultList) {
                if (scanResult.getSn().equals(device.getSn()))
                    isHas = true;
            }
            Log.d(TAG,"isHas======" + isHas);
            if (!isHas) {
                if (device != null) {
                    mState = ScanState.SCANNING_WITH_ROBOT;
                    refreshSearchUI();
                    robotScanResultList.add(device);
                    mMiniDeviceAdapter.onNotifyDataSetChanged(robotScanResultList);
                }
            }
        }
    }

    private void initView() {
        mSearchWithRobotRl = findViewById(R.id.search_withrobot_rl);
        mSearchingRobotLl = findViewById(R.id.searching_ll);
        mSearchTimeOutRl = findViewById(R.id.search_norobot_rl);
        mResultLv = findViewById(R.id.result_lv);
        mResultLv.setOnItemClickListener(this);
        tvHelper = findViewById(R.id.help);
        mPbmoredevice = findViewById(R.id.pb_moredevice);
        tvHelper.setOnClickListener(clickListener);
        tvCannotFind = findViewById(R.id.cannot_find_hint);
        tvReSearch = findViewById(R.id.open_bluetooth);
        tvReSearch.setOnClickListener(clickListener);
        mBackIv = findViewById(R.id.iv_close);
        mBackIv.setOnClickListener(clickListener);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanFailed(BleScanFailEvent event) {
        Log.d(TAG, " scanSuccess -- scanFailed: " + event.errorCode);
        mState = ScanState.SCANINIG_STOPSCAN;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanFinished(BleScanFinishedEvent event) {
        Log.i(TAG, "停止蓝牙搜索");
        mState = ScanState.SCANINIG_STOPSCAN;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(isConnecting){
            return;
        }
        UbtBluetoothHelper.getInstance().stopScan();
        mState = ScanState.CONNECT;
        refreshSearchUI();
        connectMini(robotScanResultList.get(position));
        ubtBluetoothDevice = robotScanResultList.get(position);
    }

    /**
     * 连接机器人蓝牙
     *
     * @param connectDevice 连接设备
     */
    private void connectMini(UbtBluetoothDevice connectDevice) {
        connectSN = connectDevice.getSn();
//        robotScanResultList.remove(connectDevice);
//        robotScanResultList.add(0,connectDevice);
        mMiniDeviceAdapter.setConnecting(connectSN);
        Log.d(TAG, "connectSN==" + connectSN);
        isConnecting = true;
        connectBleDevice(connectDevice);
    }


    private void connectBleDevice(final UbtBluetoothDevice device) {
        if (device != null) {
            mBluetoothDevice = device;
            mBangdingManager.setBangdingListener(mBandingListenerAbster);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UbtBluetoothManager.getInstance().closeConnectBle();
                    mHandler.sendEmptyMessage(MSG_WATH_DISCONNECT_SUCCESS);
                }
            }).start();
        }
    }

    private void showHelperDialog() {
        final CannotSearchAlphaMiniDialog dialog = new CannotSearchAlphaMiniDialog(this);

        dialog.setTips2Text(getResources().getString(R.string.searching_bluetooth_help_message_3));
        dialog.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mState = ScanState.SCANNING;
                refreshSearchUI();
              startScanBluetooth();
            }
        });
     /*   mDialog.setConnectCustomerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageRouter.toCallActivity(SearchBluetoothActivity.this, "4006666700");
                mDialog.dismiss();
            }
        });*/
        dialog.setTitle(R.string.searching_bluetooth_help_tips);

/*        mDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });*/
        dialog.show();
    }

    AnalysisClickListener clickListener = new AnalysisClickListener() {
        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()) {
                case R.id.help:
                    showHelperDialog();
                    break;
                case R.id.open_bluetooth:
                    UbtBluetoothManager.getInstance().startScanBluetooth();
                    mState = ScanState.SCANNING;
                    refreshSearchUI();
                    break;
                case R.id.iv_close:
                    onBackPressed();
                    break;
            }
        }
    };

  private void startScanBluetooth() {
    UbtBluetoothManager.getInstance().startScanBluetooth();
  }

  @Override
    public void onBackPressed() {
        super.onBackPressed();
        UbtBluetoothManager.getInstance().closeConnectBle();//主动停止--地方1
        finish();
    }

    @Override protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private Timer mTimeOutTimer;
    private TimerTask mTimeOutTask;

    private void startTimeOutTimer(){
        mTimeOutTimer = new Timer("time_out");
        mTimeOutTask = new TimerTask() {
            @Override
            public void run() {
                stopTimeOutTimer();
                Log.d(TAG, " startTimeOutTimer ");
                dismissDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowErrorDialogUtil.bleNetworkError(CONNECT_TIME_OUT_ERROR_CODE, SearchBluetoothActivity.this);
                    }
                });
            }
        };
        mTimeOutTimer.schedule(mTimeOutTask,60 * 1000);
    }

    private void stopTimeOutTimer(){
        if(mTimeOutTimer != null){
            mTimeOutTimer.cancel();
            mTimeOutTimer = null;
        }
        if(mTimeOutTask != null){
            mTimeOutTask.cancel();
            mTimeOutTask = null;
        }
    }

    private void showByOtherDialog(){
        if(mByOtherBandingDialog== null){
            mByOtherBandingDialog = new MaterialDialog(this);
        }
        mByOtherBandingDialog.setBackgroundResource(R.drawable.dialog_bg_shap)
                .setMessage(R.string.ble_by_other)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(UbtBluetoothManager.getInstance().isChangeWifi()) {
                            PageRouter.toAlphaInfoActivityTop(SearchBluetoothActivity.this);
                        } else {
                            PageRouter.toBangdingActivity(SearchBluetoothActivity.this);
                            ActivityManager.getInstance().popAllActivity();
                        }
                    }
                }).show();
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {
        mMiniDeviceAdapter.setConnecting(device.getSn());

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSubscribeBindingSuccessEvent(BindingRobotSuccessEvent event) {
//        Log.v("Logic", "1233123123123123");
//        String serail = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
//        toastSuccess("机器人绑定成功");
//        TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(serail);
//        stopTimeOutTimer();
//        dismissDialog();
//        RobotsViewModel.get().getMyRobots();
//        PageRouter.toMain(SearchBluetoothActivity.this, serail);
//    }

    BandingListenerAbster mBandingListenerAbster = new BandingListenerAbster(){
        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            isConnecting = false;
            mMiniDeviceAdapter.setConnecting("");
            String serail = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            if(UbtBluetoothManager.getInstance().isChangeWifi()) {
                toastSuccess("切网成功");
            } else {
                toastSuccess("机器人绑定成功");
                TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(serail);
            }
            stopTimeOutTimer();
            dismissDialog();
            RobotsViewModel.get().getMyRobots();
            PageRouter.toMain(SearchBluetoothActivity.this,serail);
        }

        @Override
        public void onFaild(int errorCode) {
            Log.d(TAG,"errorCode======" + errorCode);
            isConnecting = false;
            stopTimeOutTimer();
            dismissDialog();
            mMiniDeviceAdapter.setConnecting("");
            ShowErrorDialogUtil.bleNetworkError(errorCode, SearchBluetoothActivity.this, new ShowErrorDialogUtil.IRetry() {
                @Override
                public void retry() {
                    if(ubtBluetoothDevice != null) {
                        for(UbtBluetoothDevice ubtBluetoothDevice : robotScanResultList) {
                            if(ubtBluetoothDevice.getSn().equals(ubtBluetoothDevice.getSn())) {
                                // 重新连接
                                connectMini(ubtBluetoothDevice);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void devicesConnectByOther() {
            isConnecting = false;
            mMiniDeviceAdapter.setConnecting("");
            showByOtherDialog();
        }

        @Override
        public void bindByOthers(CheckBindRobotModule.User user) {
            isConnecting = false;
            mMiniDeviceAdapter.setConnecting("");
            String serial = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            PageRouter.toRequestContrRightActivity(SearchBluetoothActivity.this,user,serial);
            finish();
        }

        @Override
        public void robotNotWifi() {
            isConnecting = false;
            mMiniDeviceAdapter.setConnecting("");
            if(Utils.isWifiAvailable(SearchBluetoothActivity.this)){
                String ssid = Utils.getSSID(SearchBluetoothActivity.this).replace("\"","");
                String cap = Utils.getWifiCap(SearchBluetoothActivity.this);
                String secure = WifiStatusHelper.getSecure(ssid, SearchBluetoothActivity.this);
                if (!secure.contains("WPA")
                        && !secure.contains("wpa")
                        && !secure.contains("WEP")
                        && !secure.contains("wep")
                        && !secure.contains("EAP")) {
                    Log.d(TAG, " ==== no passwd ====");
                    // 无密码情况
                    showLoadingDialog();
                    startTimeOutTimer();
                    JsonCommandProduce commandProduce = new JsonCommandProduce();
                    String message = commandProduce.getWifiPasswdInfo(secure, ssid, "");
                    UbtBluetoothManager.getInstance().sendMessageToBle(message);
                } else {
                PageRouter.toConnectWifiActivity(SearchBluetoothActivity.this,ssid,cap);
                finish();
                }
            }else {
                PageRouter.toChooseWifiActivity(SearchBluetoothActivity.this);
                finish();
            }
        }

        @Override
        public void connectFailed() {
            isConnecting = false;
            mMiniDeviceAdapter.setConnecting("");
            stopTimeOutTimer();
            refreshSearchUI();
            dismissDialog();
            toastError("连接蓝牙失败");
        }
    };
}
