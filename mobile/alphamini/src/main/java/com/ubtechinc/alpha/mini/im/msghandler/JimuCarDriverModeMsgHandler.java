package com.ubtechinc.alpha.mini.im.msghandler;

import android.content.Intent;
import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarDriveMode;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.alpha.mini.ui.car.CarActivity;
import com.ubtechinc.alpha.mini.ui.car.CarRemindActivity;
import com.ubtechinc.alpha.mini.ui.car.CarWaitingConnectActivity;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

import com.ubtechinc.alpha.mini.ui.car.CarMsgManager;

/**
 * Created by riley.zhang on 2018/7/3.
 */

public class JimuCarDriverModeMsgHandler implements IMsgHandler {

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarDriverModeMsgHandler responseCmdId = " + responseCmdId);
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarDriveMode.ChangeJimuDriveModeResponse response = (JimuCarDriveMode.ChangeJimuDriveModeResponse)ProtoBufferDispose.unPackData(JimuCarDriveMode.ChangeJimuDriveModeResponse.class, bytes);
        int driveMode = CarMsgManager.EXIT_DRIVE_MODE;
        if (response.getDriveMode() == JimuCarDriveMode.DriveMode.QUIT) {
            driveMode = CarMsgManager.EXIT_DRIVE_MODE;
//            EventBus.getDefault().post(new JimuCarDriverModeEvent(driveMode));//目前的逻辑是通知只有退出才操作
            Intent intent = new Intent(AlphaMiniApplication.getInstance(), CarActivity.class);
            AlphaMiniApplication.getInstance().startActivity(intent);
        }
//        else if (response.getDriveMode() == JimuCarDriveMode.DriveMode.ENTER) {
//            driveMode = CarMsgManager.ENTER_DRIVE_MODE;
//        }

    }
}
