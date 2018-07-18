package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtechinc.alpha.ConnectRobotWifi;
import com.ubtechinc.alpha.GetRobotWifiList;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.adapter.WifiAdapter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.UbtScanResult;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.im.IMCmdId.IM_GET_WIFI_LIST_TO_ROBOT_REQUEST;
import static com.ubtechinc.alpha.im.IMCmdId.IM_SEND_WIFI_TO_ROBOT_REQUEST;
import static com.ubtechinc.alpha.im.IMCmdId.IM_VERSION;
import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_TIME_OUT_ERROR_CODE;

/**
 * @author：wululin
 * @date：2017/11/13 10:00
 * @modifier：ubt
 * @modify_date：2017/11/13 10:00
 * [A brief description]
 * version
 */

public class SettingChooseWifiActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = SettingChooseWifiActivity.class.getSimpleName();
    private static final int MSG_WATH_GET_WIFI_LIST = 0x001;
    private static final int MSG_WATH_GET_WIFI_LIST_ERROR = 0x002;
    private static final int MSG_WATH_WIFI_RESPONSE = 0x003;
    private static final int MSG_WATH_WIFI_RESPONSE_ERROR = 0x004;
    private ImageButton mBackBtn;
    private ListView mWifiLv;
    private WifiAdapter mWifiAdapter;
    private List<UbtScanResult> mRobotWifiList = new ArrayList<>();
    private MiniNetwrokProgressDailog mProgressDailog;
    private LinearLayout mCanntFindWifiLl;
    private TextView mSearchAgainTv;
    private ProgressBar mLoadingPb;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WATH_GET_WIFI_LIST:
                    List<UbtScanResult> scanResultList = (List<UbtScanResult>) msg.obj;
                    if(scanResultList == null){
                        mCanntFindWifiLl.setVisibility(View.VISIBLE);
                    }else {
                        mCanntFindWifiLl.setVisibility(View.GONE);
                        mLoadingPb.setVisibility(View.INVISIBLE);
                        mRobotWifiList.addAll(scanResultList);
                        Utils.sortList(mRobotWifiList);
                        mWifiAdapter.setData(mRobotWifiList);
                    }
                    break;
                case MSG_WATH_GET_WIFI_LIST_ERROR:
                    mCanntFindWifiLl.setVisibility(View.VISIBLE);
                    break;
                case MSG_WATH_WIFI_RESPONSE:
                    mProgressDailog.dismiss();
                    stopTimeOutTimer();
                    String code = (String) msg.obj;
                    if(code.equals(Constants.SUCCESS_CODE)){
                        toast("切换WiFi成功");
                        finish();
                        ActivityManager.getInstance().popActivity(SettingConnectWiffActivity.class.getName());
                    }else if (code.equals(Constants.PWD_ERROR_CODE)){
//                        toast("WiFi密码错误");
                        ShowErrorDialogUtil.showPwdErrorDialog(SettingChooseWifiActivity.this);
                    }else if(code.equals(Constants.TIME_OUT_CODE)){
                        toast("连接超时");
                        ShowErrorDialogUtil.showConnectTimeoutDialog(SettingChooseWifiActivity.this);
                    }else if(code.equals(Constants.ARLEADY_CONNECT_WIFI)){
                        toast("你已经连接了该WiFi");
                    }
                    break;
                case MSG_WATH_WIFI_RESPONSE_ERROR:
                    mProgressDailog.dismiss();
                    stopTimeOutTimer();
                    ShowErrorDialogUtil.showPhoneNetworkErrorDialog(SettingChooseWifiActivity.this);
//                    toast("设置机器人WiFi错误");
                    break;
            }
        }
    };

    public  void showRobotWifiErrorDialog(){
        final MaterialDialog aterialDialog = new MaterialDialog(this);

        aterialDialog.setTitle(R.string.wifi_invisible).setMessage(R.string.robot_wifi_unable_msg)
                .setPositiveButton(R.string.change_wifi, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                }).setNegativeButton(R.string.simple_message_cancel,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                aterialDialog.dismiss();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap).show();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_wifi);
        initView();
        initListener();
        getWifiListToRobot(0);

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

    public void getWifiListToRobot(int code){
        String mRobotUserId = MyRobotsLive.getInstance().getRobotUserId();
        GetRobotWifiList.GetRobotWifiListRequest.Builder builder =  GetRobotWifiList.GetRobotWifiListRequest.newBuilder();
        builder.setStatus(code);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IM_GET_WIFI_LIST_TO_ROBOT_REQUEST,IM_VERSION,null,mRobotUserId,mICallback);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDailog.hide();
                        Log.d(TAG, " startTimeOutTimer -- run -- CONNECT_TIME_OUT_ERROR_CODE");
                        ShowErrorDialogUtil.bleNetworkError(CONNECT_TIME_OUT_ERROR_CODE, SettingChooseWifiActivity.this);
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
                if(Utils.isNetworkAvailable(SettingChooseWifiActivity.this)){
                    sendWifiToRobot(scanResult.getS(), scanResult.getC());
                }else {
                    toast("请连接网络");
                }
            } else {
                String ssid = scanResult.getS();
                String cap = scanResult.getC();
                PageRouter.toSettingConnectWifiActivity(this,ssid,cap);
            }
        }
    }

    AnalysisClickListener mClickListener = new AnalysisClickListener() {
        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()){
                case R.id.back_btn:
                    finish();
                    break;
                case R.id.open_bluetooth:
                    getWifiListToRobot(0);
                    mLoadingPb.setVisibility(View.VISIBLE);
                    mCanntFindWifiLl.setVisibility(View.GONE);
                    break;
            }
        }
    };
    ICallback<GetRobotWifiList.GetRobotWifiListResponse> mICallback = new ICallback<GetRobotWifiList.GetRobotWifiListResponse>() {
        @Override
        public void onSuccess(GetRobotWifiList.GetRobotWifiListResponse data) {
            String wifiList = data.getWifilist();
            Log.i(TAG,"wifiList====" +  wifiList);
            try{
                JSONObject replyJson = new JSONObject(wifiList);
                String appListStr = (String) replyJson.get(Constants.WIIF_LIST_COMMAND);
                JSONArray jsonArray = new JSONArray(appListStr);
                List<UbtScanResult> scanResultList=null;
                if(jsonArray.length()!=0){
                    scanResultList  = JsonUtils.stringToObjectList(appListStr, UbtScanResult.class);
                }
                Message msg = new Message();
                msg.what = MSG_WATH_GET_WIFI_LIST;
                msg.obj = scanResultList;
                mHandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onError(ThrowableWrapper e) {
            mHandler.sendEmptyMessage(MSG_WATH_GET_WIFI_LIST_ERROR);
        }
    };

    private void sendWifiToRobot(String ssid,String cap){
        if(mProgressDailog == null){
            mProgressDailog = new MiniNetwrokProgressDailog(this,getResources().getString(R.string.connecting),R.style.Dialog_Fullscreen);
        }
        mProgressDailog.show(this);
        startTimeOutTimer();
        String mRobotUserId = MyRobotsLive.getInstance().getRobotUserId();
        ConnectRobotWifi.ConnectRobotWifiRequest.Builder request =  ConnectRobotWifi.ConnectRobotWifiRequest.newBuilder();
        request.setCap(cap);
        request.setSsid(ssid);
        request.setPwd("");
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IM_SEND_WIFI_TO_ROBOT_REQUEST,IM_VERSION,request.build(),mRobotUserId,mICallbackSendWifi);
    }
    ICallback<ConnectRobotWifi.ConnectRobotWifiResponse> mICallbackSendWifi = new ICallback<ConnectRobotWifi.ConnectRobotWifiResponse>() {
        @Override
        public void onSuccess(ConnectRobotWifi.ConnectRobotWifiResponse data) {
            String code = data.getCode();
            Message msg = new Message();
            msg.what = MSG_WATH_WIFI_RESPONSE;
            msg.obj = code;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onError(ThrowableWrapper e) {
            mHandler.sendEmptyMessage(MSG_WATH_WIFI_RESPONSE_ERROR);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWifiListToRobot(1);
    }
}
