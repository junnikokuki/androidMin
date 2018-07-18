package com.ubtechinc.alpha.mini.im.msghandler;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.JimuCarGetBleList;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.alpha.mini.entity.CarListInfo;
import com.ubtechinc.alpha.mini.event.JimuCarListEvent;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riley.zhang on 2018/7/12.
 */

public class JimuCarCarListMsgHandler implements IMsgHandler {
    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        Log.i("msgHandler", "JimuCarCarListMsgHandler request = " + request);
        byte[] bytes = request.getBodyData().toByteArray();
        JimuCarGetBleList.GetJimuCarBleListResponse response = (JimuCarGetBleList.GetJimuCarBleListResponse) ProtoBufferDispose.unPackData(JimuCarGetBleList.GetJimuCarBleListResponse.class, bytes);
        List<JimuCarGetBleList.JimuCarBle> carList = response.getBleList();
        ArrayList<CarListInfo> carListInfos = new ArrayList<>();
        if (carList != null) {
            for (int i = 0; i < carList.size(); i++) {
                CarListInfo carListInfo = new CarListInfo();
                carListInfo.setDeviceMac(carList.get(i).getMac());
                carListInfo.setDeviceBleName(carList.get(i).getName());
                carListInfos.add(carListInfo);
            }
        }

        EventBus.getDefault().post(new JimuCarListEvent(carListInfos));
    }
}
