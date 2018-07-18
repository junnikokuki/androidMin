package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.adapter.WifiAdapter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.BandingListenerAbster;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.UbtScanResult;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_TIME_OUT_ERROR_CODE;

/**
 * @author：wululin
 * @date：2017/10/20 13:44
 * @modifier：ubt
 * @modify_date：2017/10/20 13:44
 * [A brief description]
 * wifi 列表界面
 */

public class ChooseWifiActivity extends BleNetworkBaseActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = ChooseWifiActivity.class.getSimpleName();
    private ImageButton mBackBtn;
    private ListView mWifiLv;
    private WifiAdapter mWifiAdapter;
    private List<UbtScanResult> mRobotWifiList = new ArrayList<>();
    private MiniNetwrokProgressDailog mProgressDailog;
    private LinearLayout mCanntFindWifiLl;
    private TextView mSearchAgainTv;
    private MaterialDialog mMaterialDialog;
    private ProgressBar mLoadingPb;
    private ICommandProduce commandProduce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_wifi);
        initView();
        initListener();
        commandProduce = new JsonCommandProduce();
        mBangdingManager = new BangdingManager(this);
        mBangdingManager.setBangdingListener(mBanddingListener);
        mBangdingManager.setGetWifiListListener(mGetWifiListListener);
        sendMesageToRobotWifiList();
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

    }

    private void initView(){
        mBackBtn = findViewById(R.id.back_btn);
        mWifiLv = findViewById(R.id.wifi_lv);
        mCanntFindWifiLl = findViewById(R.id.search_wifi_ll);
        mSearchAgainTv = findViewById(R.id.open_bluetooth);
        mLoadingPb = findViewById(R.id.loading_pb);
    }

    private void initListener(){
        mBackBtn.setOnClickListener(mClickListener);
        mSearchAgainTv.setOnClickListener(mClickListener);
        mWifiAdapter = new WifiAdapter(this,mRobotWifiList);
        mWifiLv.setAdapter(mWifiAdapter);
        mWifiLv.setOnItemClickListener(this);
    }

    /**
     * 向机器发送获取WiFi列表指令
     */
    private void sendMesageToRobotWifiList(){
//        String message = Utils.pactkCommandToRobot(CodeMaoConstant.WIFI_LIST_TRANS);
        String message = commandProduce.getWifiList();
        if(!TextUtils.isEmpty(message)){
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }
    }

    AnalysisClickListener mClickListener = new AnalysisClickListener() {
        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()){
                case R.id.back_btn:
                    onBackPressed();
                    break;
                case R.id.open_bluetooth:
                    sendMesageToRobotWifiList();
                    mCanntFindWifiLl.setVisibility(View.GONE);
                    break;
            }
        }
    };




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        UbtScanResult scanResult = mRobotWifiList.get(position);
        if (null != scanResult) {
            String capabilities = scanResult.getC();
            if (!capabilities.contains("WPA")
                    && !capabilities.contains("wpa")
                    && !capabilities.contains("WEP")
                    && !capabilities.contains("wep")
                    && !capabilities.contains("EAP")) {
                if(Utils.isNetworkAvailable(ChooseWifiActivity.this)){
                    startTimeOutTimer();
                    sendWifi(scanResult.getS(), scanResult.getC());
                }else {
                    showNoNetorkDialog();
                }
            } else {
                String ssid = scanResult.getS();
                String cap = scanResult.getC();
                PageRouter.toConnectWifiActivity(this,ssid,cap);
            }
        }
    }

    private Timer mTimeOutTimer;
    private TimerTask mTimeOutTask;

    private void startTimeOutTimer(){
        Log.d(TAG, " startTimeOutTimer ");
        mTimeOutTimer = new Timer("time_out");
        mTimeOutTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, " startTimeOutTimer -- run");
                stopTimeOutTimer();
                dismissProgressDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, " startTimeOutTimer -- run -- CONNECT_TIME_OUT_ERROR_CODE");
                        ShowErrorDialogUtil.bleNetworkError(CONNECT_TIME_OUT_ERROR_CODE, ChooseWifiActivity.this);
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

    private void sendWifi(String ssid,String capabilities){
        if(mProgressDailog == null){
            mProgressDailog = new MiniNetwrokProgressDailog(this,getResources().getString(R.string.connecting),R.style.Dialog_Fullscreen);
        }
        mProgressDailog.show(this);
        String message = commandProduce.getWifiPasswdInfo(capabilities, ssid, "");
        UbtBluetoothManager.getInstance().sendMessageToBle(message);

    }


    /**
     * 手机无网络对话框
     */
    private void showNoNetorkDialog(){
        UbtBluetoothManager.getInstance().sendMessageToBle(commandProduce.getNetworkNotAvailable());
        if(mMaterialDialog == null){
            mMaterialDialog = new MaterialDialog(this);
        }
        mMaterialDialog.setTitle(R.string.bangding_fialed).setMessage(R.string.phone_network_error_msg)
                .setPositiveButton(R.string.please_try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMaterialDialog.dismiss();
                    }
                }).setNegativeButton(R.string.simple_message_cancel,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap).setCanceledOnTouchOutside(true).show();
    }




    BandingListenerAbster mBanddingListener = new BandingListenerAbster() {
        @Override
        public void connWifiSuccess() {
            Log.i(TAG,"机器人联网成功");
            mProgressDailog.setLoadingTipText(R.string.banding_tips);
        }

        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            stopTimeOutTimer();
            dismissProgressDialog();
            toast("机器人绑定成功");
            String serail = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(serail);
            RobotsViewModel.get().getMyRobots();
            PageRouter.toMain(ChooseWifiActivity.this,serail);
        }

        @Override
        public void onFaild(int errorCode) {
            stopTimeOutTimer();
            dismissProgressDialog();
            ShowErrorDialogUtil.bleNetworkError(errorCode,ChooseWifiActivity.this);
        }

        @Override
        public void connectSuccess() {
            toast(R.string.connect_success);
        }
    };

    private void dismissProgressDialog(){
        if(mProgressDailog != null){
            mProgressDailog.dismiss();
        }
    }

    BangdingManager.GetWifiListListener mGetWifiListListener = new BangdingManager.GetWifiListListener() {
        @Override
        public void onGetWifiList(List<UbtScanResult> scanResultList) {

            if(scanResultList == null){
                mCanntFindWifiLl.setVisibility(View.VISIBLE);
            }else {
                mCanntFindWifiLl.setVisibility(View.GONE);
                mLoadingPb.setVisibility(View.GONE);
                mRobotWifiList.addAll(scanResultList);
                Utils.sortList(mRobotWifiList);
                mWifiAdapter.setData(mRobotWifiList);
            }
        }
    };

}
