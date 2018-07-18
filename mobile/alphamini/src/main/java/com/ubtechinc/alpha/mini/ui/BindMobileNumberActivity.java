package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityObtainMobileBinding;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.ui.setting.MobileNumberChangeEvent;
import com.ubtechinc.alpha.mini.viewmodel.MobileViewModel;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * @作者：liudongyang
 * @日期: 18/4/12 11:01
 * @描述: 绑定手机号码
 */

public class BindMobileNumberActivity extends BaseToolbarActivity {

    private ActivityObtainMobileBinding binding;

    private MobileViewModel mViewModel;

    private boolean isInCountDown = false;

    private Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_obtain_mobile);
        mViewModel = new MobileViewModel();
        mHandler = new Handler();
        initView();
        setListener();
    }

    private boolean isChangeBindNumber;
    private String mOriginPhoneNumber;
    private void initView() {
        mOriginPhoneNumber = getIntent().getStringExtra(PageRouter.INTENT_KEY_NEW_PHONE);
        String title = "";
        if (!TextUtils.isEmpty(mOriginPhoneNumber)){
            title = "绑定新手机";
            isChangeBindNumber = true;
            binding.tvTitle.setVisibility(View.GONE);
        }
        initToolbar(binding.getRoot(), title, View.GONE, false);
        initConnectFailedLayout(R.string.net_work_error, R.string.robot_connect_fail);
    }

    private String mPhoneNumber;

    private String mVertificationCode;

    private void setListener() {
        binding.etIntputMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoneNumber = s.toString();
                if (!TextUtils.isEmpty(mPhoneNumber)) {
                    binding.setHasContent(true);
                } else {
                    binding.setHasContent(false);
                }
                if (!isInCountDown){//没有倒计时，可以去修改
                    binding.btnGetCode.setText(getString(R.string.binding_obtain_code));
                }
                setNextStepEnable();
                setVertifiCodeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etInputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVertificationCode = s.toString();
                setNextStepEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etIntputMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            KeyboardUtils.showSoftInput(binding.etIntputMobile);
                        }
                    }, 200);
                }else{
                    checkInputPhoneValited();//控件失去焦点
                }
            }
        });
        binding.setHasContent(false);
        binding.btnGetCode.setEnabled(false);
        setNextStepEnable();
    }

    private void setVertifiCodeEnable() {
        if (isInCountDown){
            return;
        }

        boolean isPhoneNumber = StringUtils.isPhone(mPhoneNumber);
        if (isPhoneNumber){
            binding.btnGetCode.setEnabled(true);
        }else{
            binding.btnGetCode.setEnabled(false);
        }

    }

    private void checkInputPhoneValited() {
        if (TextUtils.isEmpty(mPhoneNumber) || mPhoneNumber.length() < 11){
            toastError(getString(R.string.login_mobile_mobile_wrong_hint_inceeds));
            return;
        }
        if (mPhoneNumber.length() > 11){
            toastError(getString(R.string.login_mobile_mobile_wrong_hint_exceeds));
            return;
        }
        if (!StringUtils.isPhone(mPhoneNumber)) {
            toastError(getString(R.string.login_mobile_rigint_toast));
            return;
        }
        //原来的手机和当前的手机号码相同
        if (!TextUtils.isEmpty(mOriginPhoneNumber) && mOriginPhoneNumber.equalsIgnoreCase(mPhoneNumber)){
            toastError(getString(R.string.binding_input_new_phone_hint));
        }
    }

    private void setNextStepEnable() {
        boolean isPhoneNumber = StringUtils.isPhone(mPhoneNumber);
        boolean isVertificationOk = (!TextUtils.isEmpty(mVertificationCode) && mVertificationCode.length() == 4);
        if (isPhoneNumber && isVertificationOk) {
            binding.btnNext.setEnabled(true);
        } else {
            binding.btnNext.setEnabled(false);
        }
    }


    private void startTimer() {
        CountDownTimer timer = new CountDownTimer(1000 * 60 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isInCountDown = true;
                binding.btnGetCode.setEnabled(false);
                binding.btnGetCode.setText(getString(R.string.login_mobile_count, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                isInCountDown = false;
                binding.btnGetCode.setEnabled(true);
                binding.btnGetCode.setText(getString(R.string.login_mobile_resend));
            }
        };
        timer.start();
    }

    @Override
    protected void onBack() {
        super.onBack();
        KeyboardUtils.hideSoftInput(BindMobileNumberActivity.this);
    }

    public void clearMobileNumber(View view) {
        binding.etIntputMobile.setText("");
    }


    public void vertification(View view) {
        String mobileNumber = binding.etIntputMobile.getText().toString();
        mViewModel.vertification(mobileNumber).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()) {
                    case SUCCESS:
                        startTimer();
                        liveResult.removeObserver(this);
                        break;
                    case FAIL:
                        toastError(((LiveResult) o).getMsg());
                        liveResult.removeObserver(this);
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }

    public void bingMobileNumber(View view) {
        KeyboardUtils.hideSoftInput(BindMobileNumberActivity.this);

        String mobileNumber = binding.etIntputMobile.getText().toString();
        String vertificationCode = binding.etInputCode.getText().toString();
        mViewModel.bindMobile(mobileNumber, vertificationCode).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()) {
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        if (isChangeBindNumber){
                            toastSuccess(getString(R.string.mobile_change_mobile_success));
                        }else{
                            RobotsViewModel.get().getMyRobots();
                            PageRouter.toMain(BindMobileNumberActivity.this);
//                            PageRouter.toBangdingActivity(BindMobileNumberActivity.this);//正常绑定手机号码
                        }
                        EventBus.getDefault().post(new MobileNumberChangeEvent(mPhoneNumber));
                        finish();
                        break;
                    case FAIL:
                        toastError(((LiveResult) o).getMsg());
                        liveResult.removeObserver(this);
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }

}
