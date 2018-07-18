package com.ubtechinc.alpha.mini.im.msghandler;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarPower;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.event.JimuCarPowerEvent;
import com.ubtechinc.alpha.mini.ui.car.CarActivity;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by riley.zhang on 2018/7/3.
 */

public class JiMuCarPowerMsgHandler implements IMsgHandler {

    private static final int ROBOT_POWER = 20;
    private static final int CAR_POWER = 5;
	
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JiMuCarPowerMsgHandler request = " + request);
        int lowPowerType = 0;
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarPower.GetJimuCarPowerResponse response = (JimuCarPower.GetJimuCarPowerResponse)ProtoBufferDispose.unPackData(JimuCarPower.GetJimuCarPowerResponse.class, bytes);
        int robotPower = response.getRobotPower().getPowerPercentage();
        int carPower = response.getCarPower().getPowerPercentage();
        Log.i("msgHandler","JiMuCarPowerMsgHandler robotPower = " + robotPower + " carPower = " + carPower);
        if ((robotPower > 0 && robotPower < ROBOT_POWER) || (carPower < CAR_POWER && carPower > 0)) {
            if (robotPower < ROBOT_POWER && carPower < CAR_POWER) {
                lowPowerType = Constants.ALL_LOW_POWER;
            }else {
                if (carPower < CAR_POWER) {
                    lowPowerType = Constants.CAR_LOW_POWER;
                } else if (robotPower < ROBOT_POWER){
                    lowPowerType = Constants.ROBOT_LOW_POWER;
                }
            }
        }
        if (lowPowerType > 0) {
            EventBus.getDefault().post(new JimuCarPowerEvent(lowPowerType));
        }
    }
}
