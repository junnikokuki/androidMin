package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.ui.PageRouter;

/**
 * @desc : 无机器人提示界面
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class NoRobotView extends FrameLayout{
    public NoRobotView(Context context) {
        super(context);
        initView();
    }

    public NoRobotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NoRobotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_no_robot, this, true);
        findViewById(R.id.btn_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.toShareActivity(getContext());
            }
        });
    }

}
