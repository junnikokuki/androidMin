package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.R;

/**
 * Created by ubt on 2018/2/12.
 */

public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {

    protected RefreshKernel mRefreshKernel;
    protected int mBackgroundColor;
    private DensityUtil densityUtil;

    private ImageView progressView;
    private TextView tipsView;

    public CustomRefreshHeader(Context context) {
        this(context, null);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    public void onPullingDown(float v, int i, int i1, int i2) {

    }

    @Override
    public void onReleasing(float v, int i, int i1, int i2) {

    }

    private void initView(Context context, AttributeSet attrs) {
        densityUtil = new DensityUtil();
        progressView = new ImageView(context);
        tipsView = new TextView(context);
//        setBackgroundColor(context.getResources().getColor(R.color.text_gray));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        addView(progressView, densityUtil.dip2px(30),densityUtil.dip2px(30));
        addView(tipsView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tipsView.setText("正在同步联系人信息");
        tipsView.setTextColor(getResources().getColor(R.color.text_gray));
        tipsView.setTextSize(densityUtil.dip2px(12));
        Glide.with(context).load(R.drawable.motion_reflash_small).into(progressView);
        setPadding(0,densityUtil.dip2px(30),0,densityUtil.dip2px(30));
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return null;
    }

    @Override
    public void setPrimaryColors(@ColorInt int... ints) {

    }

    @Override
    public void onInitialized(RefreshKernel refreshKernel, int i, int i1) {
        this.mRefreshKernel = refreshKernel;
        this.mRefreshKernel.requestDrawBackgoundForHeader(this.mBackgroundColor);
    }

    @Override
    public void onHorizontalDrag(float v, int i, int i1) {

    }

    @Override
    public void onStartAnimator(RefreshLayout refreshLayout, int i, int i1) {

    }

    @Override
    public int onFinish(RefreshLayout refreshLayout, boolean b) {
        progressView.setVisibility(View.GONE);
        tipsView.setVisibility(View.GONE);
        return 500;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        Log.d("TAG","onStateChanged" +newState);
    }
}
