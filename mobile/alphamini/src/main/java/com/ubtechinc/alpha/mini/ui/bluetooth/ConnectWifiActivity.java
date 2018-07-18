package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.net.RegisterRobotModule;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.BandingListenerAbster;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.MiniNetwrokProgressDailog;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.command.ICommandProduce;
import com.ubtechinc.bluetooth.command.JsonCommandProduce;

import java.util.Timer;
import java.util.TimerTask;

import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_TIME_OUT_ERROR_CODE;
import static com.ubtechinc.alpha.mini.ui.PageRouter.SSID;

/**
 * @author：wululin
 * @date：2017/10/23 14:20
 * @modifier：ubt
 * @modify_date：2017/10/23 14:20
 * [A brief description]
 * version
 */

public class ConnectWifiActivity extends BleNetworkBaseActivity {
    private static final String TAG = ConnectWifiActivity.class.getSimpleName();
    private ImageButton mBackBtn;
    private TextView mWifiNameTV;
    private EditText mWifiPwdEt;
    private ImageView mShowPwdIv;
    private TextView mChooseOtherWifiTv;
    private TextView mCompateBtn;
    private String mSsid;
    private String mCap;
    private boolean isShowPasswd = false;
    private MiniNetwrokProgressDailog mProgressDailog;
    private MaterialDialog mMaterialDialog;
    private ICommandProduce commandProduce;
    private SharePreferenceHelper sharePreferenceHelper;
    private static final String VISIBLE_SYMBOL = "\r";
    private String pwd;
    private String ssid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        mSsid = getIntent().getStringExtra(SSID);
        mCap = getIntent().getStringExtra(PageRouter.CAP);
        initView();
        initListener();
        mBangdingManager = new BangdingManager(this);
        mWifiPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//密码
        updatePassword(mSsid);
        commandProduce = new JsonCommandProduce();
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

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
        mWifiPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//普通文本
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBangdingManager.setBangdingListener(mBanddingListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSsid = intent.getStringExtra(SSID);
        mCap = intent.getStringExtra(PageRouter.CAP);
        mBangdingManager = new BangdingManager(this);
        mBangdingManager.setBangdingListener(mBanddingListener);
        mWifiNameTV.setText(mSsid);
        updatePassword(mSsid);
    }

    private void updatePassword(String ssid) {
        if(ssid != null) {
            String passwordavisible = sharePreferenceHelper.getPassword(ssid);
            if(passwordavisible != null) {
                if (passwordavisible.startsWith(VISIBLE_SYMBOL)) {
                    mWifiPwdEt.setText(passwordavisible.substring(1));
                    changtEditTextInputType(false);
                } else {
                    mWifiPwdEt.setText(passwordavisible);
                    changtEditTextInputType(true);
                }
            }
        }
    }

    private void initListener(){
        mBackBtn.setOnClickListener(mClickListener);
        mCompateBtn.setOnClickListener(mClickListener);
        mChooseOtherWifiTv.setOnClickListener(mClickListener);
        mWifiPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if(Utils.isNetworkAvailable(ConnectWifiActivity.this)){
                        sendWifi();//进入WiFi信息发送页面
                    }else {
                        showNoNetorkDialog();
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
        mShowPwdIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changtEditTextInputType(isShowPasswd = !isShowPasswd);
            }
        });
    }

    private void sendWifi(){
        pwd = mWifiPwdEt.getText().toString().trim();
        ssid = mSsid;
        boolean isValidata = Utils.comfirmWifiInfoValidate(mSsid,pwd,mCap);
        if(isValidata){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0); //强制隐藏键盘
            if(mProgressDailog == null){
                mProgressDailog = new MiniNetwrokProgressDailog(this,getResources().getString(R.string.connecting),R.style.Dialog_Fullscreen);
            }
            mProgressDailog.show(this);
            startTimeOutTimer();
            String message = commandProduce.getWifiPasswdInfo(mCap, mSsid, pwd);
            UbtBluetoothManager.getInstance().sendMessageToBle(message);
        }else {
            toast("请输入合法的WiFi密码");
        }
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
                dismissProgressDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowErrorDialogUtil.bleNetworkError(CONNECT_TIME_OUT_ERROR_CODE, ConnectWifiActivity.this);
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
        public void connWifiSuccess() {
            Log.i(TAG,"机器人联网成功 -- mSsid : " + mSsid + " pwd : " + pwd + " isShowPasswd : " + isShowPasswd);
            mProgressDailog.setLoadingTipText(R.string.banding_tips);
            if(mSsid != null && pwd != null) {
                if(!isShowPasswd) {
                    pwd = VISIBLE_SYMBOL + pwd;
                }
                sharePreferenceHelper.putSsidAndPassword(mSsid, pwd);
            }
        }

        @Override
        public void onSuccess(RegisterRobotModule.Response response) {
            stopTimeOutTimer();
            dismissProgressDialog();
            String serail = UbtBluetoothManager.getInstance().getCurrentDeviceSerial();
            if(UbtBluetoothManager.getInstance().isChangeWifi()){
                toast("切换WiFi成功");
            }else {
                toast("机器人绑定成功");
                TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(serail);
            }
            RobotsViewModel.get().getMyRobots();
            PageRouter.toMain(ConnectWifiActivity.this, serail);
        }

        @Override
        public void onFaild(int errorCode) {
            Log.i(TAG,"errorCode========" + errorCode);
            stopTimeOutTimer();
            dismissProgressDialog();
            ShowErrorDialogUtil.bleNetworkError(errorCode,ConnectWifiActivity.this);
            changtEditTextInputType(false);

        }

        @Override
        public void connectSuccess() {
            Log.i(TAG,"connectSuccess====11111=======");
            toast(R.string.connect_success);
        }

    };

    private void dismissProgressDialog(){
        //Log.d(TAG, " dismissProgressDialog stack : " + Log.getStackTraceString(new Throwable()));
        if(mProgressDailog != null){
            mProgressDailog.dismiss();
        }
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
                    PageRouter.toChooseWifiActivity(ConnectWifiActivity.this);
                    break;
                case R.id.complate_btn:
                    if(Utils.isNetworkAvailable(ConnectWifiActivity.this)){
                        sendWifi();
                    }else {
                        showNoNetorkDialog();
                    }
                    break;
            }
        }
    };

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
            mWifiPwdEt.setSelection(pwd.length()+1);
        }
        this.isShowPasswd = isShowPasswd;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UbtBluetoothManager.getInstance().closeConnectBle();
    }

}
