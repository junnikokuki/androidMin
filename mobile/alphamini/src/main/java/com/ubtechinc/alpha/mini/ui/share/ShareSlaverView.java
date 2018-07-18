package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.alpha.mini.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @作者：liudongyang
 * @日期: 18/4/17 18:15
 * @描述: 分享
 */

public class ShareSlaverView extends LinearLayout {

    private ImageView mAvatar;

    private TextView  mTextName;

    private TextView  mTextManager;

    private View mDivier;

    public ShareSlaverView(Context context) {
        super(context);
        initView();
    }

    public ShareSlaverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShareSlaverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_share_slaver, this, true);

        mTextManager = findViewById(R.id.tv_detail_flag);
        mAvatar      = findViewById(R.id.iv_user_icon);
        mTextName    = findViewById(R.id.tv_name);
        mDivier      = findViewById(R.id.divider);

    }

    public void setMaster(boolean isMaster){
        if (isMaster){
            mTextManager.setVisibility(View.VISIBLE);
        }else {
            mTextManager.setVisibility(View.GONE);
        }
    }

    public void hideDivider(){
        mDivier.setVisibility(View.GONE);
    }


    public void setUserName(String userName) {
        mTextName.setText(userName);
    }

    public void setPicUrl(String picUrl) {
        Glide.with(getContext()).load(picUrl).bitmapTransform(new CropCircleTransformation(getContext())).crossFade(1000).into(mAvatar);
    }

}
