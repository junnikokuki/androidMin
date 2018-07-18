package com.ubtechinc.codemaosdk.interfaces;

import com.ubtechinc.codemaosdk.bean.RobotAction;

import java.util.List;

/**
 * Created by tanghongyu on 2018/3/12.
 */

public interface ILamp {


    public interface ControlCallback {

        void onSuccess();
        void onError(int code);

    }
}
