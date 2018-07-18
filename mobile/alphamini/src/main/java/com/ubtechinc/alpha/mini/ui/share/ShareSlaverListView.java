package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.Collections;
import java.util.List;

/**
 * @desc : 显示从账号列表View
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/2
 * 从帐号查看共享列表
 */

public class ShareSlaverListView extends LinearLayout {


    public ShareSlaverListView(Context context) {
        super(context);
        initView();
    }

    public ShareSlaverListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShareSlaverListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_share_slaver_check_view, this, true);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setResultBeen(List<CheckBindRobotModule.User> resultBeen) {
        int count = getChildCount();
        for (int i = count - 1; i > 0; i--) {
            removeViewAt(i);
        }
        if (!CollectionUtils.isEmpty(resultBeen)) {
            Collections.sort(resultBeen);
            Log.i("0427", "setSlaverUserList: " + resultBeen );
            for (int i = 0; i < resultBeen.size(); i++){
                CheckBindRobotModule.User user = resultBeen.get(i);
                ShareSlaverView shareSlaverView = new ShareSlaverView(getContext());
                shareSlaverView.setPicUrl(user.getUserImage());
                shareSlaverView.setUserName(user.getNickName());
                if (user.getUpUser() == 0){
                    shareSlaverView.setMaster(true);
                }else{
                    shareSlaverView.setMaster(false);
                }
                if (i == resultBeen.size() - 1){
                    shareSlaverView.hideDivider();
                }
                addView(shareSlaverView);
                requestLayout();
            }
        }
    }

}
