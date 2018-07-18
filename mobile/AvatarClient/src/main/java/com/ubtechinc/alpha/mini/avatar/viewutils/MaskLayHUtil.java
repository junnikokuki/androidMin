package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.R;

/**
 * @作者：liudongyang
 * @日期: 18/5/23 19:39
 * @描述:
 */

public class MaskLayHUtil {
    private OnMaskClickListener listener;

    private View view;

    private RelativeLayout errContent;

    private ProgressBar mProgressBar;

    private TextView mTvErrTitle;

    private TextView mTvRetry;

    public MaskLayHUtil(View view) {
        this.view = view;
        init();
    }

    public void setOnMaskClickListener(final OnMaskClickListener listener) {
        this.listener = listener;
        mTvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.doStart();
            }
        });
    }


    private void init() {
        errContent = view.findViewById(R.id.rl_error_content_h);
        mTvRetry = view.findViewById(R.id.tv_retry_h);
        mTvErrTitle = view.findViewById(R.id.tv_fail_h);
        mProgressBar = view.findViewById(R.id.loading_view_h);
    }


    public void connectFailed() {
        mTvErrTitle.setText(R.string.robot_connect_failed);
        switchView(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        switchView(View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
    }

    public void startVideoFailed() {
        mTvErrTitle.setText(R.string.robot_start_video_fail);
        switchView(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
    }

    public void networkSuck() {
        mTvErrTitle.setText(R.string.robot_avatar_network_suck);
        switchView(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
    }

    public void networkError() {
        mTvErrTitle.setText(R.string.robot_avatar_network_error);
        switchView(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
    }


    public void close() {
        switchView(View.GONE, View.GONE, View.GONE, View.GONE);
    }

    private void switchView(int contentVisible, int titleVisible, int retryVisible, int mProgressVisiable) {
        errContent.setVisibility(contentVisible);
        mTvErrTitle.setVisibility(titleVisible);
        mTvRetry.setVisibility(retryVisible);
        mProgressBar.setVisibility(mProgressVisiable);
    }


}
