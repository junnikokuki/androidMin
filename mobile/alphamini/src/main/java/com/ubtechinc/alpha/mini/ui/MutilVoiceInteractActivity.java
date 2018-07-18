package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMultiVoiceInteractBinding;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.MultiConvStateViewModel;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.SwitchButton;


/**
 * @作者：liudongyang
 * @日期: 18/7/13 16:04
 * @描述: 多伦交互的开关页面
 */
public class MutilVoiceInteractActivity extends BaseToolbarActivity {

    private SwitchButton mSbtnMultiInter;

    private ActivityMultiVoiceInteractBinding binding;

    private MultiConvStateViewModel mViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_multi_voice_interact);
        mViewModel = new MultiConvStateViewModel();
        findViews();
        initView();
        fetchMultiConvState();
    }


    private void findViews() {
        mSbtnMultiInter = findViewById(R.id.switchbtn_multi_interact);
        mSbtnMultiInter.setChecked(mViewModel.isConvStateOpen());
        mSbtnMultiInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行之前已经切换了状态
                if (mSbtnMultiInter.isChecked()){
                    final MaterialDialog dialog = new MaterialDialog(MutilVoiceInteractActivity.this);
                    dialog.setMessage(R.string.setting_multi_open_dialog_hint)
                            .setCanceledOnTouchOutside(false)
                            .setPositiveButton(R.string.i_know, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    syncMultiConvState(true);
                                }
                            }).show();
                }else{
                    syncMultiConvState(false);
                }
            }
        });
    }

    private void initView() {
        initToolbar(findViewById(R.id.titlebar),getResources().getString(R.string.voice_mutile_interact), View.GONE, false);
        initConnectFailedLayout(R.string.setting_multi_interact_network_fail, R.string.setting_multi_interact_im_connect_fail);
    }


    private boolean mHasGetState = true;
    private void fetchMultiConvState() {
        mViewModel.getConvState().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()) {
                    case FAIL:
                        mHasGetState = false;
                        break;
                    case SUCCESS:
                        mHasGetState = true;
                        mSbtnMultiInter.setChecked(MultiConvStateViewModel.isConvStateOpen());
                        break;
                }
                setConnectFailedLayout(isRobotConnectState(), isNetworkConnectState());
            }
        });
    }

    private Observer<LiveResult> convStateObserver = new Observer<LiveResult>() {
        @Override
        public void onChanged(@Nullable LiveResult liveResult) {
            switch (liveResult.getState()){
                case SUCCESS:
                case FAIL:
                    LoadingDialog.getInstance(MutilVoiceInteractActivity.this).dismiss();
                    mSbtnMultiInter.setChecked(MultiConvStateViewModel.isConvStateOpen());
                    break;
                case LOADING:
                    LoadingDialog.getInstance(MutilVoiceInteractActivity.this).show();
                    break;
            }
        }
    };


    private void syncMultiConvState(boolean state){
        mViewModel.setConvState(state).observe(this, convStateObserver);
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
            binding.setCanEdit(false);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_network_fail);
            return;
        }
        if (!robotConnectState){
            binding.setCanEdit(false);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_im_connect_fail);
            return;
        }
        if (!mHasGetState){
            binding.setCanEdit(false);
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.setting_multi_interact_request_fail);
            return;
        }
        binding.setCanEdit(true);
        connectFailed.setVisibility(View.GONE);
    }

}
