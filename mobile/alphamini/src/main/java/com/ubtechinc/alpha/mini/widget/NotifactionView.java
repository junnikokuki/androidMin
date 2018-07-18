package com.ubtechinc.alpha.mini.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.RobotStateView;


/**
 * Created by hongjie.xiang on 2017/12/4.
 */

public class NotifactionView {
    private Context mContext;
    private View    relativeLayout;
    private Handler handler;
    private TextView       notifactionText;
    private RobotStateView robotStateView;
    private WindowManager  windowManager ;
    private WindowManager.LayoutParams params;
    private ImageView      imageView ;
    private boolean        isDismiss = false ;

    private boolean        isControlImmeration = true;


    public NotifactionView(Context context) {
        mContext = context;
        relativeLayout = View.inflate(mContext, R.layout.notifaction, null);
        relativeLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        relativeLayout.setVisibility(View.GONE);
        notifactionText = relativeLayout.findViewById(R.id.notifaciton_text);
        imageView = relativeLayout.findViewById(R.id.notifacation_img);
        robotStateView = new RobotStateView(relativeLayout);
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //解决Android 7.1.1起不能再用Toast的问题（先解决crash）
            if(Build.VERSION.SDK_INT > 24){
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            }else{
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.gravity = Gravity.TOP | Gravity.LEFT;

        relativeLayout.setPadding(relativeLayout.getPaddingLeft(),relativeLayout.getPaddingTop() + getStatusBatHeight(),
                relativeLayout.getPaddingRight(),relativeLayout.getPaddingBottom());
        params.x =0 ;
        windowManager.addView(relativeLayout, params);
        handler =new MyHandler();
        handler.sendEmptyMessageDelayed(0x123,2000);

        robotStateView.addAnimationListener(new PopView.IAnmamtionListener() {
            @Override
            public void animationStart() {

            }

            @Override
            public void animationEnd() {
                if(isDismiss){
                    try{
                        if (relativeLayout.isAttachedToWindow()){

                            if (isControlImmeration){
                                ImmersionBar.with((Activity) mContext).statusBarDarkFont(true).fitsSystemWindows(true).init();
                            }

                            windowManager.removeView(relativeLayout);
                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }

    public void setControlImmeration(boolean controlImmeration) {
        this.isControlImmeration = controlImmeration;
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isDismiss = true;
            if (msg.what == 0x123) {
                robotStateView.dismiss();
            }
        }
    }


    public void setNotifactionText(String text,int colorId,int resId){
        isDismiss = false;

        if (isControlImmeration){
            ImmersionBar.with((Activity) mContext).statusBarDarkFont(false).fitsSystemWindows(true).init();
        }

        handler.removeMessages(0x123);
        relativeLayout.setVisibility(View.VISIBLE);
        robotStateView.show();
        notifactionText.setText(text);
        imageView.setBackground(mContext.getResources().getDrawable(resId));
        relativeLayout.setBackgroundColor(mContext.getResources().getColor(colorId));
        handler.sendEmptyMessageDelayed(0x123,2000);
    }



    public void setNotifactionText(String text,int resId){
        isDismiss = false;

        if (isControlImmeration){
            ImmersionBar.with((Activity) mContext).statusBarDarkFont(false).fitsSystemWindows(true).init();
        }

        handler.removeMessages(0x123);
        relativeLayout.setVisibility(View.VISIBLE);
        robotStateView.show();
        notifactionText.setText(text);
        imageView.setBackground(mContext.getResources().getDrawable(resId));
        relativeLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        handler.sendEmptyMessageDelayed(0x123,2000);
    }

    private int getStatusBatHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight ;
    }


}
