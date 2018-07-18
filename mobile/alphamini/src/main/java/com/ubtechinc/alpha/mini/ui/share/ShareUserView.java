package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @desc : 从账号用户View
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/2
 */

public class ShareUserView extends LinearLayout {

    private String userId;
    private String userName;
    private String relationDate;
    private TextView tvUserName;
    private TextView tvRelationDate;
    private ImageView ivUserIcon;
    private ImageView ivDetailFlag;
    private View mDivider;

    private boolean hasDetail;

    public ShareUserView(Context context) {
        super(context);
        initView();
    }

    public ShareUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShareUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setMinimumHeight((int) getContext().getResources().getDimension(R.dimen.share_user_height));
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_share_user, this, true);
        tvUserName = findViewById(R.id.tv_name);
        tvRelationDate = findViewById(R.id.tv_relationdate);
        ivUserIcon = findViewById(R.id.iv_user_icon);
        ivDetailFlag = findViewById(R.id.iv_detail_flag);
        mDivider = findViewById(R.id.divider);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.toSharePermissionUpdate(getContext(), userId, userName, relationDate);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRelationDate(String relationDate) {
        Long lRelationDate = Long.valueOf(relationDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date date = new Date(lRelationDate);
        String content = format.format(date);
        this.relationDate = content;
        final String shareTime = getResources().getString(R.string.accept_time, content);
        tvRelationDate.setText(shareTime);
    }

    public void hideDivider(){
        mDivider.setVisibility(View.GONE);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        tvUserName.setText(userName);
    }

    public void setPicUrl(String picUrl) {
        Glide.with(getContext()).load(picUrl).bitmapTransform(new CropCircleTransformation(getContext())).crossFade(1000).into(ivUserIcon);
    }


}


