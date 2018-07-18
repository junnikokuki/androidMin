package com.ubtechinc.codemaosdk.interfaces;

import com.ubtechinc.codemaosdk.bean.RobotAction;

import java.util.List;

/**
 * Created by tanghongyu on 2018/3/12.
 */

public interface IAction {

    public interface getActionListCallback{

            void onGetActionList(List<RobotAction> robotActionList);
            void onError(int code);
    }
    public interface playActionCallback{
        void onCompleted();

        void onError(int code);
    }

    public interface stopActionCallback {

        void onSuccess();
        void onError(int code);

    }
}
