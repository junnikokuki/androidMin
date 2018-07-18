package com.ubtechinc.alpha.mini.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.common.NetworkChangeManager;
import com.ubtechinc.alpha.mini.utils.Constants;
import com.ubtechinc.alpha.mini.utils.PreferencesManager;


public abstract class BaseContactActivity extends BaseActivity implements NetworkChangeManager.NetworkChangedListener {

    protected Context mContext;
    protected AlphaMiniApplication mApplication;
    public RelativeLayout topLayout;
    public TextView title;
    public BaseHandler mHandler;
    public LinearLayout btn_back;


    protected Context context = null;
    protected PreferencesManager preferences;
    protected ProgressDialog pg = null;
    protected NotificationManager notificationManager;
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base);
        setHandler();
        mContext = this;
        registerHandler();
        this.topLayout = (RelativeLayout) findViewById(R.id.top_title);
        this.title = (TextView) findViewById(R.id.title);
        this.topLayout.setVisibility(View.GONE);
        this.btn_back = (LinearLayout) findViewById(R.id.btn_back);

        context = this;
        mApplication = (AlphaMiniApplication) context.getApplicationContext();
        //初始化Preference
        preferences = PreferencesManager.getInstance(context);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //初始化进度对话框
        pg = new ProgressDialog(context);
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new FrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new LinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new RelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

    public abstract void registerHandler();

    public abstract void unregisterHandler();


    public void onBack(View v) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            this.finish();
        }
        /**
         * 隐藏键盘
         */
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void setTitle(String titleStr) {
        title.setText(titleStr);
    }

    public void setTitle(int titleId) {
        title.setText(this.getString(titleId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterHandler();

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    protected void setHandler() {

    }

    public BaseHandler getmHandler() {
        return mHandler;
    }


    @Override
    public void onNetworkChanged(int type) {
        if (type == Constants.NetWork.NO_CONNECTION) {
            netWorkFailed();
        } else if (type == Constants.NetWork.IS_MOBILE || type == Constants.NetWork.IS_WIFI) {
            netWorkOk(type);
        }
    }

    /**
     * 网络可用
     */
    protected void netWorkOk(int type) {

    }

    /**
     * 网络不可用
     */
    protected void netWorkFailed() {

    }

    public class BaseHandler extends Handler {

    }
}
