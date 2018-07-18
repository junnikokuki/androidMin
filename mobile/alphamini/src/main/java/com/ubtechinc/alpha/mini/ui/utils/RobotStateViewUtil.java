package com.ubtechinc.alpha.mini.ui.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtechinc.alpha.mini.R;

/**
 * Created by ubt on 2017/11/1.
 */

public class RobotStateViewUtil implements View.OnClickListener {

    public static final String TAG = "RobotStateViewUtil";

    public static final int BTN_TAG_BING = 1;
    public static final int BTN_TAG_PROGRESS = 2;
    public static final int BTN_TAG_SUCCESS = 3;
    public static final int BTN_TAG_RETRY = 4;
    public static final int BTN_TAG_NET_RETRY = 5;
    public static final int BTN_TAG_NETWORK_ERROR = 6;
    public static final int BTN_TAG_SWITCH_ROBOT = 7;
    public static final int BTN_TAG_OFFLINE_RETRY = 8;

    public static final int NETWORK_RETRY_CLICK_EVENT_ID = 10000;
    public static final int OFFLINE_RETRY_CLICK_EVENT_ID = 10001;
    public static final int DIALOG_DISMISS_TIMER_EVENT_ID = 10002;

    public static final int DIALOG_DISMISS_TIME = 3 * 1000;
    public static final int DIALOG_LOADING_TIME = 1000;//点击重试，出现loading的时间

    View rootView;

    TextView tvStateTitle;
    TextView tvStateContent;
    LinearLayout llAnimationBg;
    ImageView ivAnimaIcon;
    TextView tvConnectHint;
    ImageView ivRobotBg;
    RelativeLayout rlBind;

    private boolean networkAvailable = true;

    private StateViewListener listener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case NETWORK_RETRY_CLICK_EVENT_ID:
                    showNetworkError();
                    break;
                case DIALOG_DISMISS_TIMER_EVENT_ID:
                    hidden();
                    break;
                case OFFLINE_RETRY_CLICK_EVENT_ID:
                    showOffLine();
                    break;
            }
            return false;
        }
    });

    public RobotStateViewUtil(View stateView, ImageView robotBg) {
        this.rootView = stateView;
        this.ivRobotBg = robotBg;
        if (stateView == null) {
            Log.e(TAG, "stateView is null");
            return;
        }
        init();
    }

    public void setNetworkAvailable(boolean available){
        networkAvailable = available;
    }

    private void init() {
        tvStateTitle = rootView.findViewById(R.id.tv_state_title);
        tvStateContent = rootView.findViewById(R.id.tv_state_content);
        llAnimationBg = rootView.findViewById(R.id.ll_animation_bg);
        ivAnimaIcon = rootView.findViewById(R.id.iv_state_animation);
        tvConnectHint = rootView.findViewById(R.id.tv_connect_message);
        rlBind = rootView.findViewById(R.id.rl_robot_bind);
        tvStateContent.setOnClickListener(this);
        tvConnectHint.setOnClickListener(this);
        rlBind.setOnClickListener(this);
    }

    public void hidden() {
        Log.d(TAG, "hidden: ");
        if (rootView != null) {
            rootView.setVisibility(View.GONE);
        }
        ivRobotBg.setImageResource(R.drawable.img_home_robot);
    }

    public void showOffLine(){
        if (!networkAvailable){
            Log.d(TAG, "showBinding: network error");
            return;
        }
        Log.d(TAG, "showOffLine: " + rootView);
        stopDismissTimer();
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            tvStateTitle.setText(R.string.robot_offline);
            rlBind.setVisibility(View.GONE);
            tvStateContent.setVisibility(View.VISIBLE);
            tvStateContent.setText(R.string.retry);
            tvStateContent.setTag(BTN_TAG_OFFLINE_RETRY);
            setRobotBgRes();
            setBtnVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        }

    }

    public void showBinding() {
        if (!networkAvailable){
            Log.d(TAG, "showBinding: network error");
            return;
        }
        Log.d(TAG, "showBinding " +rootView );
        if (rootView != null) {
            if(handler != null){
                if (handler.hasMessages(DIALOG_DISMISS_TIMER_EVENT_ID)) {
                    handler.removeMessages(DIALOG_DISMISS_TIMER_EVENT_ID);
                }
            }
            rootView.setVisibility(View.VISIBLE);
            tvStateTitle.setText(R.string.bind_mini_please);
            rlBind.setVisibility(View.VISIBLE);
            rlBind.setTag(BTN_TAG_BING);
            setRobotBgRes();
            setBtnVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
            tvStateContent.setVisibility(View.GONE);

        }
    }

    public void showConnectting() {
        if (!networkAvailable){
            Log.d(TAG, "showConnectting: network error");
            return;
        }
        stopDismissTimer();
        Log.d(TAG, "showConnectting " +rootView );
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            Glide.with(rootView.getContext()).load(R.drawable.connect_loading).into(new GlideDrawableImageViewTarget(ivAnimaIcon,100));
            tvConnectHint.setText(R.string.robot_connecting);
            tvConnectHint.setTag(BTN_TAG_PROGRESS);
            rlBind.setVisibility(View.GONE);
            setRobotBgRes();
            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
        }
    }

    public void showSwitching() {
        if (!networkAvailable) {
            Log.d(TAG, "showSwitching: network error");
            return;
        }
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            Glide.with(rootView.getContext()).load(R.drawable.connect_loading).into(new GlideDrawableImageViewTarget(ivAnimaIcon, 100));
            tvConnectHint.setText(R.string.switching);
            tvConnectHint.setTag(BTN_TAG_PROGRESS);
            rlBind.setVisibility(View.GONE);
            setRobotBgRes();
            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
        }
    }

//    public void showSwitchingRotbot(){
//        if (!networkAvailable){
//            Log.d(TAG, "showConnectting: network error");
//            return;
//        }
//        Log.d(TAG, "showSwitchRotbot " +rootView );
//        if (rootView != null) {
//            rootView.setVisibility(View.VISIBLE);
//            Glide.with(rootView.getContext()).load(R.drawable.connect_loading).into(new GlideDrawableImageViewTarget(ivAnimaIcon,100));
//            tvConnectHint.setText(R.string.switching_robot);
//            tvConnectHint.setTag(BTN_TAG_SWITCH_ROBOT);
//            setRobotBgRes();
//            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
//        }
//    }

    public void showConnectFail() {
        if (!networkAvailable){
            Log.d(TAG, "showConnectFail: network error");
            return;
        }
        stopDismissTimer();
        Log.d(TAG, "showConnectFail " +rootView );
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            tvStateTitle.setText(R.string.robot_connect_fail);
            rlBind.setVisibility(View.GONE);
            tvStateContent.setVisibility(View.VISIBLE);
            tvStateContent.setText(R.string.retry);
            tvStateContent.setTag(BTN_TAG_RETRY);
            setRobotBgRes();
            setBtnVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void showConnectSuccess() {
        if (!networkAvailable){
            Log.d(TAG, "showConnectSuccess: network error");
            return;
        }
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            Glide.with(rootView.getContext()).load(R.drawable.connect_success).into(new GlideDrawableImageViewTarget(ivAnimaIcon,1));
            tvConnectHint.setText(R.string.robot_connect_success);
            tvConnectHint.setTag(BTN_TAG_SUCCESS);
            rlBind.setVisibility(View.GONE);
            setRobotBgRes();
            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
        }
        startDismissTimer();
    }

    public void showSwitchSuccess() {
        if (!networkAvailable){
            Log.d(TAG, "showConnectSuccess: network error");
            return;
        }
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
            Glide.with(rootView.getContext()).load(R.drawable.connect_success).into(new GlideDrawableImageViewTarget(ivAnimaIcon,1));
            tvConnectHint.setText(R.string.switch_success);
            tvConnectHint.setTag(BTN_TAG_SUCCESS);
            rlBind.setVisibility(View.GONE);
            setRobotBgRes();
            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
        }
    }

    public void showNetworkError(){
        Log.d(TAG, "showNetworkError: network error");
        stopDismissTimer();
        if (rootView != null){
            rootView.setVisibility(View.VISIBLE);
            tvStateTitle.setText(R.string.network_unavaliable);
            rlBind.setVisibility(View.GONE);
            tvStateContent.setVisibility(View.VISIBLE);
            tvStateContent.setText(R.string.go_to_connect);
            tvStateContent.setTag(BTN_TAG_NETWORK_ERROR);
            setRobotBgRes();
            setBtnVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        }
    }

    private void setRobotBgRes(){
        if (rootView.getVisibility() == View.VISIBLE){
            ivRobotBg.setImageResource(R.drawable.img_home_robot_open_hand);
        }else{
            ivRobotBg.setImageResource(R.drawable.img_robot);
        }
    }


    private void stopDismissTimer(){
        if (handler.hasMessages(DIALOG_DISMISS_TIMER_EVENT_ID)){
            handler.removeMessages(DIALOG_DISMISS_TIME);
        }
    }

    private void startDismissTimer(){
        handler.sendEmptyMessageDelayed(DIALOG_DISMISS_TIMER_EVENT_ID, DIALOG_DISMISS_TIME);
    }

    private void errorRetryClick(int resId, int reponseEventId){
        if (rootView != null){
            rootView.setVisibility(View.VISIBLE);
            Glide.with(rootView.getContext()).load(R.drawable.connect_loading).into(new GlideDrawableImageViewTarget(ivAnimaIcon,100));
            tvConnectHint.setText(resId);
            setBtnVisibility(View.GONE, View.GONE,View.VISIBLE, View.VISIBLE);
        }
        handler.sendEmptyMessageDelayed(reponseEventId, DIALOG_LOADING_TIME);
    }

    private void setBtnVisibility(int stateTitleVisi, int stateContentVisi, int llAnimationVisi, int connectHintVisi) {
        tvStateTitle.setVisibility(stateTitleVisi);
        tvStateContent.setVisibility(stateContentVisi);
        llAnimationBg.setVisibility(llAnimationVisi);
        tvConnectHint.setVisibility(connectHintVisi);
    }

    @Override
    public void onClick(View view) {
        int tag = 0;
        Object object = view.getTag();
        if(object != null){
            tag = (int) view.getTag();
        }
        switch (tag) {
            case BTN_TAG_BING:
                if (listener != null) {
                    listener.bind();
                }
                break;
            case BTN_TAG_NET_RETRY: //TODO
                break;
            case BTN_TAG_PROGRESS:
                break;
            case BTN_TAG_SWITCH_ROBOT:
                break;
            case BTN_TAG_SUCCESS:
                hidden();
                break;
            case BTN_TAG_RETRY:
                if (listener != null) {
                    listener.connect();
                }
                break;
            case BTN_TAG_OFFLINE_RETRY:
                if (listener != null) {
                    listener.connect();
                }
                break;
            case BTN_TAG_NETWORK_ERROR:
//                errorRetryClick(R.string.connecting, NETWORK_RETRY_CLICK_EVENT_ID);
                if (listener != null) {
                    listener.netConnect();
                }
                break;
        }
    }

    public boolean isShowing(){
        return rootView.getVisibility() == View.VISIBLE;
    }

    public void setListener(StateViewListener listener) {
        this.listener = listener;
    }

    public interface StateViewListener {

        public void bind();

        public void connect();

        public void netConnect();

    }
}
