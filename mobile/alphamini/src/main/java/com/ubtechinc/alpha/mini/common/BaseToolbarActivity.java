package com.ubtechinc.alpha.mini.common;

import android.arch.lifecycle.Observer;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.utils.NetUtil;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

public abstract class BaseToolbarActivity extends BaseActivity {

    protected View toolbar;

    protected ImageView backBtn;

    protected TextView titleView;

    protected ImageView actionBtn;


    protected TextView rightText;

    protected TextView leftText;

    private View divdeLine;

    /**机器人的连接状态**/
    private boolean robotConnectState;
    /**手机端的网络状态**/
    private boolean networkConnectState;

    /**在网络或者机器人连接的情况中，是否关闭了连接异常的layout**/
    private boolean isColsedConnectLayout;
    /**手机网络或者机器人连接异常的Layout**/
    protected View connectFailed;
    protected TextView tvConnectedText;
    protected ImageView ivColseConnectFailed;

    @StringRes
    private int networkErrorResId;

    @StringRes
    private int connectErrorResId;


    private NetworkHelper.NetworkInductor inductor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getNetworkState();
        getRobotConnectState();
        registerListener();
    }

    /**
     * 初始化Toolbar 必须调用initToolBar才能使用控件
     * @param toolbar toolbar控件
     * @param title  顶部title文案
     * @param actionBtnVisibility  右边的ImageView是否显示
     * @param actionBtnEnable   右边的button是否可以点击
     */
    protected void initToolbar(View toolbar, String title, int actionBtnVisibility, boolean actionBtnEnable) {
        this.toolbar = toolbar;
        titleView = findViewById(R.id.toolbar_title);
        backBtn = findViewById(R.id.toolbar_back);
        actionBtn = findViewById(R.id.toolbar_action);
        rightText = findViewById(R.id.right_text_content);
        leftText = findViewById(R.id.left_text_content);
        divdeLine = findViewById(R.id.view_border);
        setTitle(title);
        setActionBtnEnable(actionBtnEnable);
        setActionBtnVisibility(actionBtnVisibility);
        actionBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
        leftText.setOnClickListener(onClickListener);
        rightText.setOnClickListener(onClickListener);
    }

    protected void initConnectFailedLayout(@StringRes int networkErrorRes, @StringRes int connectErrorRes) {
        this.networkErrorResId = networkErrorRes;
        this.connectErrorResId = connectErrorRes;
        //连接失败部分View初始化
        connectFailed = findViewById(R.id.layout_connect);
        if(connectFailed == null){
            return;
        }
        ivColseConnectFailed = connectFailed.findViewById(R.id.iv_close_connect_layout);
        tvConnectedText = connectFailed.findViewById(R.id.tv_error_info);
        ivColseConnectFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isColsedConnectLayout = true;
                connectFailed.setVisibility(View.GONE);
            }
        });
    }

    protected void onBack() {
        finish();
    }

    protected void onAction(View view) {

    }

    protected void setTitle(String title) {
        titleView.setText(title);
    }

    protected void setActionBtnVisibility(int visibility) {
        actionBtn.setVisibility(visibility);
    }

    protected void setActionBtnEnable(boolean enable) {
        actionBtn.setEnabled(enable);
    }


    protected void needDivideLine(boolean showDivide) {
        divdeLine.setVisibility(showDivide ? View.VISIBLE : View.GONE);
    }

    protected void setLeftImageRes(int resId){
        if (backBtn != null){
            backBtn.setImageResource(resId);
        }
    }


    protected boolean isRobotConnectState() {
        return robotConnectState;
    }

    protected boolean isNetworkConnectState() {
        return networkConnectState;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbar_back:
                case R.id.left_text_content:
                    onBack();
                    break;
                case R.id.toolbar_action:
                case R.id.right_text_content:
                    onAction(view);
                    break;
            }
        }
    };

    private void registerListener() {
        inductor = new NetworkHelper.NetworkInductor() {
            @Override
            public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
                    networkConnectState = true;
                }else{
                    networkConnectState = false;
                }
                setConnectFailedLayout(robotConnectState, networkConnectState);
            }
        };
        NetworkHelper.sharedHelper().addNetworkInductor(inductor);
        MyRobotsLive.getInstance().getCurrentRobot().observe(this, new Observer<RobotInfo>() {
            @Override
            public void onChanged(@Nullable RobotInfo robotInfo) {
                observerCurrentRobot(robotInfo);
                if (robotInfo != null){
                    switch (robotInfo.getOnlineState()) {
                        case RobotInfo.ROBOT_STATE_OFFLINE:
                            robotConnectState = false;
                            break;
                        case RobotInfo.ROBOT_STATE_ONLINE:
                            robotConnectState = true;
                            break;
                        case RobotInfo.ROBOT_STATE_UNAVAILABLE:
                            robotConnectState = false;
                            break;
                    }
                }
                setConnectFailedLayout(robotConnectState, networkConnectState);
            }
        });
    }

    /**
     * 暴露给子类监听当前机器人状态变化,如果子类再添加监听的话，会有问题。
     * 当被解绑的时候，可能会为空
     * @param robotInfo 当前机器人的信息
     */
    protected void observerCurrentRobot(@Nullable RobotInfo robotInfo){

    }

    /**
     * 子类监听顶部连接的
     * @param robotConnectState
     * @param networkConnectState
     */
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if (isColsedConnectLayout){
            return;
        }
        if(connectFailed == null){
            return;
        }
        if (!networkConnectState){
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(networkErrorResId);
            return;
        }
        if (!robotConnectState){
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(connectErrorResId);
            return;
        }
        connectFailed.setVisibility(View.GONE);
    }

    protected boolean isColsedConnectLayout() {
        return isColsedConnectLayout;
    }

    protected void setColsedConnectLayout(boolean colsedConnectLayout) {
        isColsedConnectLayout = colsedConnectLayout;
    }


    protected boolean getRobotConnect(){
        return robotConnectState;
    }

    protected boolean getNetworkConnectState(){
        return networkConnectState;
    }

    /**
     * 获取网络状态
     */
    private void getNetworkState() {
        if (NetUtil.isNetWorkConnected(this)){
            networkConnectState = true;
        }else{
            networkConnectState = false;
        }
    }

    private void getRobotConnectState() {
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (robotInfo == null){
            robotConnectState = false;
            return;
        }
        if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE){
            robotConnectState = true;
        }else{
            robotConnectState = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkHelper.sharedHelper().removeNetworkInductor(inductor);
    }


}
