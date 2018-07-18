package com.ubtechinc.alpha.mini.im.msghandler;

import android.content.Intent;
import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarCheck;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.ui.car.DetectedResultActivity;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import java.util.List;

/**
 * Created by riley.zhang on 2018/7/11.
 */

public class JimuCarCheckMagHandler implements IMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarCheckMagHandler request = " + request);
        boolean servoError = false;
        boolean irError = false;
        boolean motorError = false;
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarCheck.checkCarResponse response = (JimuCarCheck.checkCarResponse)ProtoBufferDispose.unPackData(JimuCarCheck.checkCarResponse.class, bytes);
        List<Integer> errorIds = response.getErrorModelIdsList();
        if (errorIds != null && errorIds.size() > 0) {
            for (int i = 0; i < errorIds.size(); i++) {
                Log.i("test","JimuCarCheckMagHandler errorId = " + errorIds.get(i));
                if (errorIds.get(i) == 0x02 || errorIds.get(i) == 0x03) {
                    servoError = true;
                } else if (errorIds.get(i) == 0x04 || errorIds.get(i) == 0x05) {
                    motorError = true;
                } else if(errorIds.get(i) == 0x06) {
                    irError = true;
                }
            }
        }
        int errorCode = -1;
        Log.i("msgHandler","JimuCarCheckMagHandler irError = " + irError + " motorError = " + motorError);
        if (irError || motorError) {
            if (irError && motorError) {
                errorCode = 3;
            }else {
                if (irError) {
                    errorCode = 2;
                } else if (motorError){
                    errorCode = 1;
                }
            }
            Intent intent = new Intent(AlphaMiniApplication.getInstance(), DetectedResultActivity.class);
            intent.putExtra("data", errorCode);
            AlphaMiniApplication.getInstance().startActivity(intent);
        }
    }
}
