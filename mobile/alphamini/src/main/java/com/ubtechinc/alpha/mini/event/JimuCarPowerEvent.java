package com.ubtechinc.alpha.mini.event;

/**
 * Created by riley.zhang on 2018/7/4.
 */

public class JimuCarPowerEvent {
    int mLowPowerType;

    public JimuCarPowerEvent(int lowPowerType) {
        mLowPowerType = lowPowerType;
    }

    public int getLowPowerType() {
        return mLowPowerType;
    }
}
