package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.ui.adapter.PopupRobotAdapter;

public class RobotPresentWindow extends PopupWindow implements View.OnClickListener {

    private Context context;

    private BaseFragment.IPresentListener presentListener;

    private PopupRobotAdapter popupRobotAdapter;

    public RobotPresentWindow(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.popu_present, null), ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight() / 2);
        this.context = context;
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        setHeight(metrics.heightPixels);
        getContentView().setOnClickListener(this);
        getContentView().findViewById(R.id.tv_next).setOnClickListener(this);
        getContentView().findViewById(R.id.tv_receive).setOnClickListener(this);
    }

    public void show(View anchor) {
        setFocusable(false);
        update();
        showAtLocation(anchor, Gravity.CENTER, 0, 0);
        fullScreenImmersive(getContentView());
        setFocusable(true);
        update();
    }

    public void setPresentListener(BaseFragment.IPresentListener presentListener) {
        this.presentListener = presentListener;
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
        if(presentListener != null) {
            switch (v.getId()) {
                case R.id.tv_receive:
                    presentListener.onReceive();
                    break;
                case R.id.tv_next:
                    presentListener.onClose();
                    break;
            }
        }
    }

    public void fullScreenImmersive(Window window) {
        if (window != null) {
            fullScreenImmersive(window.getDecorView());
        }
    }

    public void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }
}
