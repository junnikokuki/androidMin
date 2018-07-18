package com.ubtechinc.alpha.mini.ui.upgrade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;

/**
 * Created by ubt on 2018/4/8.
 */

public class UpgradeStateFragment extends BaseFragment{

    public static final String TAG = "UpgradeStateFragment";

    public static final int STATE_LOADING = 0;
    public static final int STATE_ROBOT_OFFLINE = 1;
    public static final int STATE_MOBILE_OFFLINE = 2;
    public static final int STATE_FAIL = 3;

    private View loadingView;
    private View robotOfflineView;
    private View mobileOfflineView;
    private View failView;

    private int state = STATE_LOADING;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade_state,null,false);
    }

    private void init(){
        loadingView = getActivity().findViewById(R.id.upgrade_loading);
        robotOfflineView = getActivity().findViewById(R.id.upgrade_robot_offline);
        mobileOfflineView = getActivity().findViewById(R.id.upgrade_mobile_offline);
        failView = getActivity().findViewById(R.id.upgrade_fail);
        setState(this.state);
    }

    public void setState(int state){
        LogUtils.d(TAG,"upgradestate = " + state);
        if (loadingView != null) {
            switch (state) {
                case STATE_LOADING:
                    showLoading();
                    break;
                case STATE_ROBOT_OFFLINE:
                    showMobileOffline();
                    break;
                case STATE_MOBILE_OFFLINE:
                    showRobotOffline();
                    break;
                case STATE_FAIL:
                    showFail();
                    break;
                default:
                    showLoading();
                    break;
            }
        }else{
            this.state = state;
        }

    }

    public void showLoading(){
        loadingView.setVisibility(View.VISIBLE);
        robotOfflineView.setVisibility(View.GONE);
        mobileOfflineView.setVisibility(View.GONE);
        failView.setVisibility(View.GONE);
    }

    public void showRobotOffline(){
        loadingView.setVisibility(View.GONE);
        robotOfflineView.setVisibility(View.GONE);
        failView.setVisibility(View.GONE);
        mobileOfflineView.setVisibility(View.VISIBLE);
    }

    public void showMobileOffline(){
        loadingView.setVisibility(View.GONE);
        robotOfflineView.setVisibility(View.VISIBLE);
        mobileOfflineView.setVisibility(View.GONE);
        failView.setVisibility(View.GONE);
    }

    public void showFail(){
        loadingView.setVisibility(View.GONE);
        robotOfflineView.setVisibility(View.GONE);
        mobileOfflineView.setVisibility(View.GONE);
        failView.setVisibility(View.VISIBLE);
    }
}
