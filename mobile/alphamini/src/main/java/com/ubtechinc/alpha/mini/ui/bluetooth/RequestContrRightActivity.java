package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.AnalysisClickListener;
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity;
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.event.RequestContronRightEvent;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.viewmodel.AccountApplyViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.UbtBluetoothDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ubtechinc.alpha.mini.ui.PageRouter.SERIAL;

/**
 * @Date: 2017/10/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class RequestContrRightActivity extends BleNetworkBaseActivity {
    private static final String TAG = RequestContrRightActivity.class.getSimpleName();
    // 申请共享间隔时间
    private static final long REQUEST_DURATION_TIME = 5 * 60 * 1000;
    private CheckBindRobotModule.User mUser;
    private TextView mUserNameBigTv;
    private TextView mUserNameSmallTv;
    private TextView mApplyShareTv;
    private TextView mApplyShareBtn;
    private TextView mToMainTv;
    private ImageView mBackIv;
    private String robotUserId;

    MaterialDialog applyErrorDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_control_right);
        mUser = (CheckBindRobotModule.User) getIntent().getSerializableExtra(PageRouter.USER);
        initView();
        initListener();
        initData();
        EventBus.getDefault().register(this);
        if (mUser != null) {
            String nickName = mUser.getNickName();
            String binding_tip = String.format(getResources().getString(R.string.request_right_title_tips), nickName);
            mUserNameBigTv.setText(binding_tip);
            String reqeust_tip = String.format(getResources().getString(R.string.request_control_right_hint), nickName);
            mUserNameSmallTv.setText(reqeust_tip);
        }

    }

    private void initData() {
        Intent intent = getIntent();
        robotUserId = intent.getStringExtra(SERIAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClickable()) {
            toRequestBtnClickable();
        } else {
            toRequestBtnUnClickable();
            startResumeRask(REQUEST_DURATION_TIME - getRequestDuraion());
        }
    }


    private Runnable resumeTask = new Runnable() {
        @Override
        public void run() {
            toRequestBtnClickable();
        }
    };

    private void startResumeRask(long time) {
        mApplyShareTv.postDelayed(resumeTask, time);
    }

    private void initView() {
        mUserNameBigTv = findViewById(R.id.user_name_big_tv);
        mUserNameSmallTv = findViewById(R.id.user_name_small_tv);
        mApplyShareTv = findViewById(R.id.apply_share_tv);
        mApplyShareBtn = findViewById(R.id.apply_share_btn);
        mToMainTv = findViewById(R.id.to_main_tv);
        mBackIv = findViewById(R.id.iv_close);
    }

    private void initListener() {
        mBackIv.setOnClickListener(mClickListener);
        mToMainTv.setOnClickListener(mClickListener);
        mApplyShareBtn.setOnClickListener(mClickListener);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RequestContronRightEvent event) {
        Log.i(TAG, "request : " + event.isAllow());
        PageRouter.toMain(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplyShareTv.removeCallbacks(resumeTask);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onReconnectDevice(UbtBluetoothDevice device) {

    }

    AnalysisClickListener mClickListener = new AnalysisClickListener() {

        @Override
        public void onClick(View view, boolean reported) {
            switch (view.getId()) {
                case R.id.iv_close:
                    finish();
                    break;
                case R.id.to_main_tv:
                    PageRouter.toMain(RequestContrRightActivity.this);
                    break;
                case R.id.apply_share_btn:
                    final LiveResult liveResult = AccountApplyViewModel.getInstance().applyRobot(robotUserId);
                    final TextView tv = (TextView) view;
                    startResumeRask(REQUEST_DURATION_TIME);
                    liveResult.observe(RequestContrRightActivity.this, new Observer<LiveResult>() {
                        @Override
                        public void onChanged(@Nullable LiveResult liveResult1) {
                            if (liveResult.getState() == LiveResult.LiveState.SUCCESS) {
                                SPUtils.get().put(Constants.REQUEST_SHARE_TIME + robotUserId+ AuthLive.getInstance().getUserId(), System.currentTimeMillis());
                                toRequestBtnUnClickable();
                                toastSuccess(getResources().getString(R.string.request_share_success));
                            } else if (liveResult.getState() == LiveResult.LiveState.FAIL) {
                                toRequestBtnClickable();
                                showMsgDialog(liveResult.getMsg());
                            }
                        }
                    });
                    break;
            }
        }
    };

    private void toRequestBtnUnClickable() {
        mApplyShareBtn.setVisibility(View.GONE);
        mApplyShareTv.setVisibility(View.VISIBLE);
    }

    private void toRequestBtnClickable() {
        mApplyShareBtn.setVisibility(View.VISIBLE);
        mApplyShareTv.setVisibility(View.GONE);
    }

    /**
     * 申请共享按钮是否有效
     */
    private boolean isClickable() {
        return getRequestDuraion() > REQUEST_DURATION_TIME;
    }

    /**
     * 获取发送请求的时间间隔
     */
    private long getRequestDuraion() {
        Long requestShareTime = SPUtils.get().getLong(Constants.REQUEST_SHARE_TIME + robotUserId + AuthLive.getInstance().getUserId());
        return System.currentTimeMillis() - requestShareTime;
    }

    public void showMsgDialog(String message) {
        if (applyErrorDialog != null) {
            applyErrorDialog.dismiss();
        }else{
            applyErrorDialog = new MaterialDialog(this);
        }
        applyErrorDialog.setMessage(message);
        applyErrorDialog.setTitle(null);
        applyErrorDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyErrorDialog.dismiss();
            }
        });
        applyErrorDialog.show();
    }


}
