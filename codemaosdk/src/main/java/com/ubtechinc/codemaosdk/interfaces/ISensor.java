package com.ubtechinc.codemaosdk.interfaces;

/**
 * @Deseription 传感器相关
 * @Author tanghongyu
 * @Time 2018/4/3 12:42
 */

public interface ISensor {

    interface GetInfraredDistance{
        void onGetDistance(int distance);
        void onFail(int code);
    }

    interface ObserveBattery {
        void onGetBattery(int level, int state);
    }

    interface ObserveHeadRacket {
        void onHeadRacket(int type);
    }

    interface ObserveRobotPosture {
        void onRobotPosture(int type);
    }

    interface ObserveFallDownState {
        void onFallDownState(int state);
    }

    interface ObserveVolumeKeyPress {
        void onKeyPress(int type);
    }
    interface ObserveInfraredDistance {
        void onInfraredDistance(int distance);
    }
}
