package com.ubtechinc.alpha.mini.ui.msg;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMessageBinding;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.ui.PageRouter;

public class MessageActivity extends BaseToolbarActivity implements View.OnClickListener {

    ActivityMessageBinding activityMessageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        View toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.message), View.GONE, false);
//        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        activityMessageBinding.msgShare.setOnClickListener(this);
        activityMessageBinding.msgSystem.setOnClickListener(this);
        MessageLive.get().observe(this, new Observer<MessageLive>() {
            @Override
            public void onChanged(@Nullable MessageLive messageLive) {
                int shareCount = messageLive.getShareMsgCount();
                int systemCount = messageLive.getSystemMsgCount();
                String shareCountMsg = getString(R.string.without_noread_msg);
                if (shareCount > 0) {
                    shareCountMsg = getString(R.string.noread_msg_count, shareCount);
                }
                String sysCountMsg = getString(R.string.without_noread_msg);
                if (systemCount > 0) {
                    sysCountMsg = getString(R.string.noread_msg_count, systemCount);
                }
                activityMessageBinding.setShareCountInt(shareCount);
                activityMessageBinding.setSysCountInt(systemCount);
                activityMessageBinding.setShareCount(shareCountMsg);
                activityMessageBinding.setSysCount(sysCountMsg);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_share:
                PageRouter.toShareMessage(this);
                break;
            case R.id.msg_system:
                PageRouter.toSystemMessage(this);
                break;
            default:
                break;
        }
    }
}
