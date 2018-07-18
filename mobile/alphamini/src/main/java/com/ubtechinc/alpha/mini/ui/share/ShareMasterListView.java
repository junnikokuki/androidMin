package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.ui.PageRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/4/17 16:50
 * @描述: 主账号查看共享列表
 */

public class ShareMasterListView extends LinearLayout {

    private LinearLayout llShareTips;

    private List<CheckBindRobotModule.User> mSlaverList;

    public ShareMasterListView(Context context) {
        super(context);
        initView();
    }

    public ShareMasterListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShareMasterListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_share_master_check_view, this, true);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setSlaverUserList(List<CheckBindRobotModule.User> result){
        if (llShareTips == null) {
            llShareTips = findViewById(R.id.ll_share_user);
        }
        int count = getChildCount();
        for (int i = count - 1; i > 0; i--) {
            removeViewAt(i);
        }
        mSlaverList = handleSlaveList(result);
        Log.i("0416", "setSlaverUserList: " + mSlaverList);
        if (!CollectionUtils.isEmpty(mSlaverList)){
            llShareTips.setVisibility(View.VISIBLE);
            for (int i = 0 ; i < mSlaverList.size() ; i++){
                CheckBindRobotModule.User user = mSlaverList.get(i);
                ShareUserView shareUserView = new ShareUserView(getContext());
                shareUserView.setUserId(user.getUserId()+"");
                shareUserView.setUserName(user.getNickName());
                shareUserView.setPicUrl(user.getUserImage());
                shareUserView.setRelationDate(user.getRelationDate());
                if (i == mSlaverList.size() - 1){
                    shareUserView.hideDivider();
                }
                addView(shareUserView);
                requestLayout();
            }
            addShareUser();
            requestLayout();
        }else{
            llShareTips.setVisibility(View.GONE);
            addView(new NoRobotView(getContext()));
        }
    }

    private void addShareUser(){
        LayoutInflater.from(getContext()).inflate(R.layout.item_addshare_user, this, true);
        findViewById(R.id.btn_share_master).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.toShareActivity(getContext());
            }
        });
    }

    private List<CheckBindRobotModule.User> handleSlaveList(List<CheckBindRobotModule.User> result) {
        List<CheckBindRobotModule.User> lists = new ArrayList<>();
        for (CheckBindRobotModule.User user:result){
            if (user.getUpUser() == 1){
                lists.add(user);
            }
        }
        return lists;
    }


}
