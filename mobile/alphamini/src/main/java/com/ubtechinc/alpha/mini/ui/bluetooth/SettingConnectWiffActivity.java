package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.ConnectRobotWifi;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.im.IMCmdId.IM_SEND_WIFI_TO_ROBOT_REQUEST;
import static com.ubtechinc.alpha.im.IMCmdId.IM_VERSION;
import static com.ubtechinc.alpha.mini.ui.PageRouter.SSID;

/**
 * @author：wululin
 * @date：2017/11/10 17:59
 * @modifier：ubt
 * @modify_date：2017/11/10 17:59
 * [A brief description]
 * version
 */

public class SettingConnectWiffActivity extends BaseActivity {
    private static final String TAG = ConnectWifiActivity.class.getSimpleName();
    private static final int MSG_WATH_WIFI_RESPONSE = 0x001;
    private static final int MSG_WATH_WIFI_RESPONSE_ERROR = 0x002;
    private static final int MSG_WATH_STOP_WIFI_TIME = 0x003;
    private ImageButton mBackBtn;
    private TextView mWifiNameTV;
    private EditText mWifiPwdEt;
    private ImageView mShowPwdIv;
    private TextView mChooseOtherWifiTv;
    private TextView mCompateBtn;
    private String mSsid;
    private String mCap;
    private boolean isShowPasswd = true;
    private MiniNetwrokProgressDailog mProgressDailog;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WATH_WIFI_RESPONSE:
                    mProgressDailog.dismiss();
                    String code = (String) msg.obj;
                    if(code.equals(Constants.SUCCESS_CODE)){
                        toast("切换WiFi成功");
                        finish();
                        ActivityManager.getInstance().popActivity(SettingChooseWifiActivity.class.getName());
                        ActivityManager.getInstance().popActivity(SettingShowRobotWifiActivity.class.getName());
                    }else if (code.equals(Constants.PWD_ERROR_CODE)){
                        changtEditTextInputType(false);
//                        toast("WiFi密码错误");
                        ShowErrorDialogUtil.showPwdErrorDialog(SettingConnectWiffActivity.this);
                    }else if(code.equals(Constants.TIME_OUT_CODE)){
//                        toast("连接超时");
                        showRobotWifiErrorDialog();
                        changtEditTextInputType(false);
                    }else if(code.equals(Constants.ARLEADY_CONNECT_WIFI)) {
                        toastSuccess("你已经连接了该WiFi");
                    }
                    break;
                case MSG_WATH_WIFI_RESPONSE_ERROR:
                    mProgressDailog.dismiss();
                    showPhoneNetworkErrorDialog();
                    changtEditTextInputType(false);
                    break;

                case MSG_WATH_STOP_WIFI_TIME:
                    mProgressDailog.dismiss();
                    showRobotWifiErrorDialog();
                    changtEditTextInputType(false);
                    break;
            }
        }
    };

    private void showPhoneNetworkErrorDialog() {
        final MaterialDialog mNetworkErrorDialog = new MaterialDialog(this);
        mNetworkErrorDialog.setMessage(R.string.net_work_error)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNetworkErrorDialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .setBackgroundResource(R.drawable.dialog_bg_shap)
                .show();
    }

    public  void showRobotWifiErrorDialog(){
        final MaterialDialog aterialDialog = new MaterialDialog(this);

        aterialDialog.setMessage(R.string.connect_timeout)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                })
                .setBackgroundResource(R.drawable.dialog_bg_shap)
                .show();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);
        mSsid = getIntent().getStringExtra(SSID);
        mCap = getIntent().getStringExtra(PageRouter.CAP);
        initView();
        initListener();
        mWifiPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//密码
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSsid = intent.getStringExtra(SSID);
        mCap = intent.getStringExtra(PageRouter.CAP);
        mWifiNameTV.setText(mSsid);
        mWifiPwdEt.setText("");//连接失败的时候，清除保存的密码
    }

    private void initView(){
        mBackBtn = findViewById(R.id.back_btn);
        mWifiNameTV = findViewById(R.id.wifi_name_tv);
        mWifiPwdEt = findViewById(R.id.edit_wifi_passwd);
        mShowPwdIv = findViewById(R.id.iv_showpasswd);
        mChooseOtherWifiTv = findViewById(R.id.choose_other_wifi_tv);
        mCompateBtn = findViewById(R.id.complate_btn);
        mWifiNameTV.setText(mSsid);
        mWifiPwdEt.setSingleLine(true);
        mWifiPwdEt.requestFocus();
    }

    private void initListener(){
        mBackBtn.setOnClickListener(mClickListener);
        mCompateBtn.setOnClickListener(mClickListener);
        mChooseOtherWifiTv.setOnClickListener(mClickListener);
        mShowPwdIv.setOnClickListener(mClickListener);
        mWifiPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if(Utils.isNetworkAvailable(SettingConnectWiffActivity.this)){
                        isStartTime = true;
                        startSentWifiTimer();
                        sendWifiToRobot();
                    }else {
                        toast("请连接网络");
                    }
                    Editable etable = mWifiPwdEt.getText();
                    Selection.setSelection(etable, etable.length());
                    return false;
                }
                return true;
            }
        });
        mWifiPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCompateBtn.setEnabled(!TextUtils.isEmpty(mWifiPwdEt.getText().toString()));
            }
        });
    }

    AnalysisClickListener mClickListener = new AnalysisClickListener(){

        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()){
                case R.id.back_btn:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0); //强制隐藏键盘
                    onBackPressed();
                    break;
                case R.id.choose_other_wifi_tv:
                    PageRouter.toSettingChooseWifiActivity(SettingConnectWiffActivity.this);
                    break;
                case R.id.complate_btn:
                    if(Utils.isNetworkAvailable(SettingConnectWiffActivity.this)){
                        isStartTime = true;
                        startSentWifiTimer();
                        sendWifiToRobot();
                    }else {
                        toast("请连接网络");
                    }
                    break;
                case R.id.iv_showpasswd:
                    changtEditTextInputType(isShowPasswd);
                    break;
            }
        }
    };

    private Timer mTimer;
    private TimerTask mTask;
    private boolean isStartTime;
    private void startSentWifiTimer(){
        stopSendWifiTimer();
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG,"定时器结束了=========");
                isStartTime = false;
                mHandler.sendEmptyMessage(MSG_WATH_STOP_WIFI_TIME);
            }
        };
        mTimer.schedule(mTask,50*1000);
    }

    private void stopSendWifiTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        if(mTask != null){
            mTask.cancel();
            mTask = null;
        }
    }

    private void changtEditTextInputType(boolean isShowPasswd) {
        String pwd = mWifiPwdEt.getText().toString().trim();
        if (isShowPasswd) {
            mWifiPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//密码
            mShowPwdIv.setImageResource(R.drawable.ic_password_not_visible);
        } else {
            mShowPwdIv.setImageResource(R.drawable.ic_password_visible);
            mWifiPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//普通文本
        }
        if(!TextUtils.isEmpty(pwd)){
            mWifiPwdEt.setSelection(pwd.length());
        }
        this.isShowPasswd = !isShowPasswd;
    }

    private void sendWifiToRobot(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0); //强制隐藏键盘
        if(mProgressDailog == null){
            mProgressDailog = new MiniNetwrokProgressDailog(this,getResources().getString(R.string.connecting),R.style.Dialog_Fullscreen);
        }
        mProgressDailog.show(this);
        String pwd = mWifiPwdEt.getText().toString().trim();
        String mRobotUserId = MyRobotsLive.getInstance().getRobotUserId();
        ConnectRobotWifi.ConnectRobotWifiRequest.Builder request =  ConnectRobotWifi.ConnectRobotWifiRequest.newBuilder();
        request.setCap(mCap);
        request.setSsid(mSsid);
        request.setPwd(pwd);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IM_SEND_WIFI_TO_ROBOT_REQUEST,IM_VERSION,request.build(),mRobotUserId,mICallback);
    }
    ICallback<ConnectRobotWifi.ConnectRobotWifiResponse> mICallback = new ICallback<ConnectRobotWifi.ConnectRobotWifiResponse>() {
        @Override
        public void onSuccess(ConnectRobotWifi.ConnectRobotWifiResponse data) {
            Log.i(TAG,"机器人返回的code=========" + data.getCode());
            if(!isStartTime){
                return;
            }
            stopSendWifiTimer();
            String code = data.getCode();
            Message msg = new Message();
            msg.what = MSG_WATH_WIFI_RESPONSE;
            msg.obj = code;
            mHandler.sendMessage(msg);

        }

        @Override
        public void onError(ThrowableWrapper e) {
            stopSendWifiTimer();
            mHandler.sendEmptyMessage(MSG_WATH_WIFI_RESPONSE_ERROR);
        }
    };


}
