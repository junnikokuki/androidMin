package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.BandingListenerAbster;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.CannotSearchAlphaMiniDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
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
 * @date：2017/10/14 10:43
 * @modifier：ubt
 * @modify_date：2017/10/14 10:43
 * [A brief description]
 * 蓝牙自动连接界面
 */

public class BleAutoConnectActivity extends BleNetworkBaseActivity {
    private static final int GOTO_SEND_WIFI_MSG_WATH = 0x001;
    private static final int ONE_MIN = 1000;
    private static final String TAG = "BleAutoConnectActivity";
    private ImageButton mBackBtn;
    private TextView mNoResponseTv;
    private TextView mMoreCloseTv;
    private ImageView mCloseToRobotIv;
    private FrameLayout mConnectSuccessFl;
    private MaterialDialog mByOtherBandingDialog;
    private MiniNetwrokProgressDailog mProgressDailog;
    private List<UbtBluetoothDevice> ubtDevices = new ArrayList<>();
    private int tryConnectCount = 0;
    private MaterialDialog mConnectTimeoutDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOTO_SEND_WIFI_MSG_WATH:
                    if (Utils.isWifiAvailable(BleAutoConnectActivity.this)) {
                        // TODO 判断是否有密码
                        String ssid = Utils.getSSID(BleAutoConnectActivity.this).replace("\"", "");
                        String secure = WifiStatusHelper.getSecure(ssid, BleAutoConnectActivity.this);
                        if(secure != null){
                            if (!secure.contains("WPA")
                                    && !secure.contains("wpa")
                                    && !secure.contains("WEP")
                                    && !secure.contains("wep")
                                    && !secure.contains("EAP")) {
                                Log.d(TAG, " ==== no passwd ====");
                                // 无密码情况
                                mProgressDailog = new MiniNetwrokProgressDailog(BleAutoConnectActivity.this,getResources().getString(R.string.connecting),R.style.Dialog_Fullscreen);
                                mProgressDailog.show(BleAutoConnectActivity.this);
                                startTimeOutTimer();
                                JsonCommandProduce commandProduce = new JsonCommandProduce();
                                String message = commandProduce.getWifiPasswdInfo(secure, ssid, "");
                                UbtBluetoothManager.getInstance().sendMessageToBle(message);
                            } else {
                                Log.d(TAG, " ==== passwd ====");
                                String cap = Utils.getWifiCap(BleAutoConnectActivity.this);
                                PageRouter.toConnectWifiActivity(BleAutoConnectActivity.this, ssid, cap);
                                finish();
                            }
                        }else{
                            PageRouter.toChooseWifiActivity(BleAutoConnectActivity.this);
                            finish();
                        }
                    } else {
                        PageRouter.toChooseWifiActivity(BleAutoConnectActivity.this);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_auto_connect);
        initView();
        initListener();
        mBangdingManager = new BangdingManager(this);
        UbtBluetoothDevice ubtBluetoothDevice = getUbtBluetoothDevice();
        Log.d(TAG, " ubtBluetoothDevice : " + ubtBluetoothDevice);
        if(ubtBluetoothDevice != null) {
            Log.d(TAG, " connectBluetooth ");
            UbtBluetoothManager.getInstance().connectBluetooth(ubtBluetoothDevice);
            mBangdingManager.setBangdingListener(mBanddingListener);
        } else {
            initBluetooth();
        }
    }

    /**
     *  获取预备绑定对象
     * @return
     */
    private UbtBluetoothDevice getUbtBluetoothDevice() {
        Intent intent = getIntent();
        if(intent != null) {
            UbtBluetoothDevice bluetoothDevice = intent.getParcelableExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH);
            return bluetoothDevice;
        }
        return null;
    }

    private void initBluetooth() {
        UbtBluetoothManager.getInstance().setAutoConnect(true);
        UbtBluetoothManager.getInstance().setBleNamePrefix(Constants.ROBOT_TAG);
        UbtBluetoothManager.getInstance().setCurrentRobotSn(MyRobotsLive.getInstance().getRobotUserId());
        mBangdingManager.setBangdingListener(mBanddingListener);
        startScan();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanResult(BleScanResultEvent event) {
        Log.d(TAG, " scanSuccess -- device: " + event.device);
        ubtDevices.add(event.device);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanFailed(BleScanFailEvent event) {
        Log.d(TAG, " scanSuccess -- scanFailed: " + event.errorCode);
        if(CollectionUtils.isEmpty(ubtDevices)){
            showHintDialog();
        }else{
            deviceIsTooFar();
        }
        ubtDevices.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtBluetoothManager.getInstance().setAutoConnect(true);
        mBangdingManager.setBangdingListener(mBanddingListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeBleScanFinished(BleScanFinishedEvent event) {
        EventBus.getDefault().unregister(this);
    }

    private void startScan(){
        tryConnectCount++ ;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        UbtBluetoothManager.getInstance().startScanBluetooth();
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
                dismissBandingDailog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowErrorDialogUtil.bleNetworkError(CONNECT_TIME_OUT_ERROR_CODE, BleAutoConnectActivity.this);
                        UbtBluetoothManager.getInstance().startScanBluetooth();
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

    BandingListenerAbster mBanddingListener = new BandingListenerAbster() {

        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            String serial = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            if(UbtBluetoothManager.getInstance().isChangeWifi()) {
                toastSuccess("切网成功");
                finish();
            } else {
                toastSuccess("机器人绑定成功");
                TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(serial);
            }
            stopTimeOutTimer();
            RobotsViewModel.get().getMyRobots();
            PageRouter.toMain(BleAutoConnectActivity.this, serial);
            finish();

        }

        @Override
        public void connectSuccess() {
            Log.i(TAG,"connectSuccess==============");
            mConnectSuccessFl.setVisibility(View.VISIBLE);
            vibrator();
            if(!UbtBluetoothManager.getInstance().isChangeWifi()) {
                showBandingDailog();
                startTimeOutTimer();
            }
        }

        @Override
        public void onFaild(int errorCode) {
            stopTimeOutTimer();
            mConnectSuccessFl.setVisibility(View.GONE);
            dismissBandingDailog();
            ShowErrorDialogUtil.bleNetworkError(errorCode, BleAutoConnectActivity.this, new ShowErrorDialogUtil.IRetry() {
                @Override
                public void retry() {
                    initBluetooth();
                }
            });
        }

        @Override
        public void devicesConnectByOther() {
            mConnectSuccessFl.setVisibility(View.GONE);
            stopTimeOutTimer();
            dismissBandingDailog();
            showByOtherDialog();
        }

        @Override
        public void bindByOthers(CheckBindRobotModule.User user) {
            dismissBandingDailog();
            mConnectSuccessFl.setVisibility(View.GONE);
            String serial = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            PageRouter.toRequestContrRightActivity(BleAutoConnectActivity.this, user, serial);
            finish();
        }

        @Override
        public void robotNotWifi() {
            Log.i(TAG,"robotNotWifi=======");
            mHandler.sendEmptyMessageDelayed(GOTO_SEND_WIFI_MSG_WATH, ONE_MIN);
        }

        @Override
        public void connectFailed() {
            mConnectSuccessFl.setVisibility(View.GONE);
            showHintDialog();
        }
    };

    private void dismissBandingDailog() {
        //Log.d(TAG, " dismissBandingDailog : " + Log.getStackTraceString(new Throwable()));
        if (mProgressDailog != null) {
            mProgressDailog.dismiss();
        }
    }

    private void showBandingDailog() {
        if (mProgressDailog == null) {
            mProgressDailog = new MiniNetwrokProgressDailog(this, getResources().getString(R.string.banding_tips), R.style.Dialog_Fullscreen);
        }
        mProgressDailog.show(this);
    }


    private void initView() {
        mBackBtn = findViewById(R.id.back_btn);
        mNoResponseTv = findViewById(R.id.no_response_tv);
        mCloseToRobotIv = findViewById(R.id.close_to_robot_iv);
        mConnectSuccessFl = findViewById(R.id.connect_success_fl);
        mMoreCloseTv = findViewById(R.id.more_close_tv);
        Glide.with(this).load(R.drawable.close_to_robot).into(new GlideDrawableImageViewTarget(mCloseToRobotIv, 1000));

    }

    private void initListener() {
        mBackBtn.setOnClickListener(mOnClickListener);
        mNoResponseTv.setOnClickListener(mOnClickListener);
    }

    AnalysisClickListener mOnClickListener = new AnalysisClickListener() {
        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()) {
                case R.id.back_btn:
                    onBackPressed();
                    break;
                case R.id.no_response_tv:
                    mBangdingManager.setBangdingListener(null);
                    PageRouter.toSearchBluetooth(BleAutoConnectActivity.this);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UbtBluetoothManager.getInstance().closeConnectBle();
        finish();
    }

    private void deviceIsTooFar(){
        if(tryConnectCount == 1){
            showTipText();
            startScan();
        }else if(tryConnectCount > 1){
            showConnectTimeoutDialog();
        }

    }

    private void showConnectTimeoutDialog() {
        if (mConnectTimeoutDialog == null) {
            mConnectTimeoutDialog = new MaterialDialog(this);
            mConnectTimeoutDialog.setMessage(R.string.connect_timeout);
            mConnectTimeoutDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mConnectTimeoutDialog.dismiss();
                    startScan();
                }
            });
        }
        mConnectTimeoutDialog.show();
    }

    private void showTipText(){
        mMoreCloseTv.setVisibility(View.VISIBLE);
        mMoreCloseTv.startAnimation(AnimationUtils.loadAnimation(this,R.anim.tips_anim));
    }

    public void showHintDialog() {
        CannotSearchAlphaMiniDialog dialog = new CannotSearchAlphaMiniDialog(this);
        dialog.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startScan();
            }
        });
        dialog.show();
    }


    private void vibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeOutTimer();
        mHandler.removeMessages(GOTO_SEND_WIFI_MSG_WATH);
        mHandler.removeCallbacks(null);
        mBangdingManager.setBangdingListener(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

    }

    private void showByOtherDialog() {
        if (mByOtherBandingDialog == null) {
            mByOtherBandingDialog = new MaterialDialog(this);
        }
        mByOtherBandingDialog.setBackgroundResource(R.drawable.dialog_bg_shap)
                .setMessage(R.string.ble_by_other)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(UbtBluetoothManager.getInstance().isChangeWifi()) {
                            PageRouter.toAlphaInfoActivityTop(BleAutoConnectActivity.this);
                        } else {
                            PageRouter.toBangdingActivity(BleAutoConnectActivity.this);
                            ShowErrorDialogUtil.finishAllWifiAcitivity();
                        }
                    }
                }).show();
    }

}
