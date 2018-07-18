package com.ubtechinc.alpha.mini.ui.friend;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ubtechinc.alpha.CmCameraPrivacy;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityCameraPrivacyBinding;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.viewmodel.CameraPrivacyViewModel;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

/**
 * @作者：liudongyang
 * @日期: 18/7/16 11:11
 * @描述: 摄像头隐私
 */
public class AlphaMiniCameraPrivacyActivity extends BaseToolbarActivity implements View.OnClickListener {

    private ActivityCameraPrivacyBinding mBinding;

    private CameraPrivacyViewModel mPrivacyViewModel;

    private boolean mHasGetState = true;

    private static final String NEED_SHOW_PRIVACY_DIALOG = "need_show_privacy_dialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_privacy);
        mPrivacyViewModel = new CameraPrivacyViewModel();
        initView();
        setListener();
        getComunicationState();
    }




    private void initView() {
        initToolbar(findViewById(R.id.titlebar),getResources().getString(R.string.app_conmunication_mode), View.GONE, false);
        initConnectFailedLayout(R.string.app_conmunication_network_fail, R.string.app_conmunication_im_connect_fail);
        mBinding.switchbtnPrivacy.setChecked(CameraPrivacyViewModel.isEnable());
    }


    private void setListener() {
        mBinding.switchbtnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBinding.switchbtnPrivacy.isChecked()){
                    setComunicationState(false);
                    return;
                }
                if (SharedPreferencesUtils.getBoolean(AlphaMiniCameraPrivacyActivity.this,NEED_SHOW_PRIVACY_DIALOG, true)){
                    setupPrivacyHintDialog();
                }else{
                    setComunicationState(true);
                }
            }
        });
        mBinding.tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.toAgreementActivity(AlphaMiniCameraPrivacyActivity.this);
            }
        });
    }

    private MaterialDialog mDialog;
    private CheckBox mCbNoMoreHint;
    private void setupPrivacyHintDialog() {
        mDialog = new MaterialDialog(AlphaMiniCameraPrivacyActivity.this);
        View view = LayoutInflater.from(AlphaMiniCameraPrivacyActivity.this).inflate(R.layout.layout_dialog_privacy, null);
        view.findViewById(R.id.tv_agree).setOnClickListener(this);
        view.findViewById(R.id.tv_disagree).setOnClickListener(this);
        mCbNoMoreHint = view.findViewById(R.id.cb_no_more_hint);
        TextView textView = view.findViewById(R.id.tv_agreement);
        SpannableString spanString = new SpannableString(textView.getText());
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PageRouter.toAgreementActivity(AlphaMiniCameraPrivacyActivity.this);
            }
        }, 13, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan spannable = new ForegroundColorSpan(ContextCompat.getColor(AlphaMiniCameraPrivacyActivity.this, R.color.color_communication_privacy));
        spanString.setSpan(spannable, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        mDialog.setContentView(view).setCanceledOnTouchOutside(true)
                .setBackground(ContextCompat.getDrawable(AlphaMiniCameraPrivacyActivity.this, R.drawable.comunication_dialog_window))
                .show();
    }


    private void getComunicationState() {
        mPrivacyViewModel.getComunicationState().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()){
                    case SUCCESS:
                        mHasGetState = true;
                        CmCameraPrivacy.CameraPrivacyType type = (CmCameraPrivacy.CameraPrivacyType) liveResult.getData();
                        if (type.getNumber() == CmCameraPrivacy.CameraPrivacyType.ON_VALUE){
                            mBinding.switchbtnPrivacy.setChecked(true);
                        }else{
                            mBinding.switchbtnPrivacy.setChecked(false);
                        }
                        break;
                    case FAIL:
                        mHasGetState = false;
                        break;
                }
                setConnectFailedLayout(isRobotConnectState(), isNetworkConnectState());
            }
        });
    }

    private void setComunicationState(final boolean enable){
        mPrivacyViewModel.setComunicationState(enable).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()){
                    case SUCCESS:
                        LoadingDialog.getInstance(AlphaMiniCameraPrivacyActivity.this).dismiss();
                        mBinding.switchbtnPrivacy.setChecked(enable);
                        break;
                    case FAIL:
                        LoadingDialog.getInstance(AlphaMiniCameraPrivacyActivity.this).dismiss();
                        mBinding.switchbtnPrivacy.setChecked(!enable);
                        break;
                    case LOADING:
                        LoadingDialog.getInstance(AlphaMiniCameraPrivacyActivity.this).show();
                        break;
                }
            }
        });
    }




    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if (isColsedConnectLayout()){
            return;
        }
        if(connectFailed == null){
            return;
        }
        if (!networkConnectState){
            mBinding.setCanEdit(false);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_network_fail);
            return;
        }
        if (!robotConnectState){
            mBinding.setCanEdit(true);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_im_connect_fail);
            return;
        }
        if (!mHasGetState){
            mBinding.setCanEdit(false);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_request_fail);
            return;
        }
        mBinding.setCanEdit(true);
        connectFailed.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_agree:
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    setComunicationState(true);
                    if (mCbNoMoreHint.isChecked()){
                        SharedPreferencesUtils.putBoolean(AlphaMiniCameraPrivacyActivity.this,NEED_SHOW_PRIVACY_DIALOG, false);
                    }else{
                        SharedPreferencesUtils.putBoolean(AlphaMiniCameraPrivacyActivity.this,NEED_SHOW_PRIVACY_DIALOG, true);
                    }
                }
                break;
            case R.id.tv_disagree:
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                break;
        }
    }
}
