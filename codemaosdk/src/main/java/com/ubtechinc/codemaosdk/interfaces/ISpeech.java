package com.ubtechinc.codemaosdk.interfaces;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/3/30 14:48
 */

public interface ISpeech {

    public interface PlayTTSCallback {
        void onBegin();

        void onCompleted();

        void onError(int code);
    }

    public interface ControlCallback {

        void onSuccess();
        void onError(int code);

    }
}
