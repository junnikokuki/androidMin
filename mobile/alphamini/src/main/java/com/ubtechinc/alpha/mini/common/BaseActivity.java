package com.ubtechinc.alpha.mini.common;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.ui.LoginActivity;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.WelcomeActivity;
import com.ubtechinc.alpha.mini.ui.msg.MessageDetailActivity;
import com.ubtechinc.alpha.mini.utils.AuthObserveHelper;
import com.ubtechinc.alpha.mini.utils.CleanLeakUtils;
import com.ubtechinc.alpha.mini.utils.LeakUtils;
import com.ubtechinc.alpha.mini.utils.MsgObserveHelper;
import com.ubtechinc.alpha.mini.utils.RomUtil;
import com.ubtechinc.alpha.mini.viewmodel.AuthViewModel;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.NotifactionView;

public class BaseActivity extends LifecycleActivity {

    public static final String TAG = "BaseActivity";
    MaterialDialog msgDialog;
    private AuthViewModel authViewModel;

    NotifactionView notifactionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatuesBar();

        if (null != savedInstanceState) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        ActivityManager am = ActivityManager.getInstance();
        am.pushActivity(this);
        initData();
    }

    protected void setStatuesBar(){
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .statusBarColor(android.R.color.white)
                .hideBar(BarHide.FLAG_SHOW_BAR)
                .fitsSystemWindows(true)
                .init();
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            //OPPO
            setOPPOStatusTextColor(true, this);
        }
    }

    public void showLoadingDialog() {
        LoadingDialog.getInstance(this).show();
    }

    public void dismissDialog() {
        LoadingDialog.dissMiss();
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toastSuccess(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean canPopNotifyView = checkCanPopNotifyView();
                if (canPopNotifyView){
                    ToastUtils.showShortToast(msg);
                    return;
                }
                notifactionView = new NotifactionView(BaseActivity.this);
                notifactionView.setNotifactionText(msg,R.drawable.ic_toast_succeed);
            }
        });
    }

    private boolean checkCanPopNotifyView() {
        int version = RomUtil.getMiuiVersion();
        if (version == -1){//小米系统
            Log.i(TAG, "checkSystemVersion: is not xiaoMi System");
            return false;
        }

        if (RomUtil.checkFloatWindowPermission(this)){
            return false;
        }
        return true;
    }



    public void toastError(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean canPopNotifyView = checkCanPopNotifyView();
                if (canPopNotifyView){
                    ToastUtils.showShortToast(msg);
                    return;
                }
                notifactionView = new NotifactionView(BaseActivity.this);
                notifactionView.setNotifactionText(msg,R.color.alexa_red_press,R.drawable.ic_toast_warn);
            }
        });

    }

    public void tips() {

    }

    public void warm() {

    }

    public void confirm() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityManager am = ActivityManager.getInstance();
        am.popActivity(this);
    }

    @Override
    protected void onDestroy() {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }
        ImmersionBar.with(this).destroy();
        CleanLeakUtils.fixInputMethodManagerLeak(this);
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.clearRecord(this);
        LeakUtils.watch(this);
    }

    private void initData() {
        new MsgObserveHelper().msgObserve(this, this, new MsgObserveHelper.MsgHandleCallback() {
            @Override
            public void handleMsg(Message message) {
                receiveMsg(message);
            }
        });
        if (!(this instanceof LoginActivity)) {
            new AuthObserveHelper().authObserve(this, this);
        }
    }

    public void showMsgDialog(String message) {
        if (msgDialog != null) {
            msgDialog.dismiss();
        } else {
            msgDialog = new MaterialDialog(this);
            msgDialog.setMessage(message);
            msgDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    msgDialog.dismiss();
                }
            });
            msgDialog.show();
        }
    }

    private void showShareApply(String noticeId, Message message) {
        PageRouter.toMessageDetail(this, noticeId, message, MessageDetailActivity.TYPE_APPLY);
    }

    protected void receiveMsg(Message message) {

    }

    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    private static void setOPPOStatusTextColor(boolean lightStatusBar, Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lightStatusBar) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (lightStatusBar) {
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            }
        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

}
