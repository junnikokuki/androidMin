package com.ubtechinc.alpha.mini.ui;

import android.view.View;

import com.ubtechinc.alpha.mini.widget.PopView;

/**
 * @Date: 2017/12/4.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人状态View的操作
 */

public class RobotStateView extends PopView {

    public RobotStateView(View view) {
        super(view);
    }

    @Override
    protected void setViewContent() {
        setPopways(PopWays.Top);
    }
}
