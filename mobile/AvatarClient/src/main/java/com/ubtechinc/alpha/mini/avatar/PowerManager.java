package com.ubtechinc.alpha.mini.avatar;

import com.ubtechinc.alpha.mini.avatar.common.Constants;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;

import java.util.Timer;
import java.util.TimerTask;

public class PowerManager {

    IAvatarRobot avatarRobot;

    OnPowerChangeListener listener;

    Timer mPowerRequestTimer;

    public PowerManager(IAvatarRobot avatarRobot, OnPowerChangeListener listener) {
        this.avatarRobot = avatarRobot;
        this.listener = listener;
    }

    /**
     * 没有获取到电量时，短间隔尝试获取电量次数
     */
    private int mTryQueryPowerCount = 5;

    private boolean mIsPowerObtained = false;

    public void start() {
        startTimer();
    }

    /*电量查询task*/
    private class PowerRequestTask extends TimerTask {

        @Override
        public void run() {
            mTryQueryPowerCount = 5;
            doPowerRequest();
        }
    }

    private void doPowerRequest() {
        if (mTryQueryPowerCount > 0) {
            mTryQueryPowerCount--;
            avatarRobot.requestPowerStatus(new IAvatarRobot.GetPowerCallback() {
                @Override
                public void onSuccess(final int powerValue) {
                    mIsPowerObtained = true;
                    if (listener != null) {
                        listener.onChange(powerValue);
                    }
                }

                @Override
                public void onError() {
                    doPowerRequest();
                }
            });
        }
    }

    private void startTimer() {
        stopTimer();
        mPowerRequestTimer = new Timer();
        mPowerRequestTimer.schedule(new PowerRequestTask(), 0, Constants.POWER_REQUEST_INTERVAL);
    }

    private void stopTimer() {
        if (mPowerRequestTimer != null) {
            mPowerRequestTimer.cancel();
            mPowerRequestTimer = null;
        }
    }

    public void stop() {
        listener = null;
        mIsPowerObtained = false;
        if (mPowerRequestTimer != null) {
            mPowerRequestTimer.cancel();
            mPowerRequestTimer = null;
        }
    }

    public OnPowerChangeListener getListener() {
        return listener;
    }

    public void setListener(OnPowerChangeListener listener) {
        this.listener = listener;
    }

    public interface OnPowerChangeListener {

        public void onChange(int powerValue);
    }
}
