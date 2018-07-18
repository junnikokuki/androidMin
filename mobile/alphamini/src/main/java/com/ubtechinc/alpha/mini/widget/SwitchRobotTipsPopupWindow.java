package com.ubtechinc.alpha.mini.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;

public class SwitchRobotTipsPopupWindow extends PopupWindow implements View.OnClickListener{

    private Activity context;

    public SwitchRobotTipsPopupWindow(final Activity context) {
        super(LayoutInflater.from(context).inflate(R.layout.lay_switch_tips, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = context;
    }

    public void show(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, -anchor.getLeft() + anchor.getWidth()/2, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        int left = anchor.getLeft();
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
