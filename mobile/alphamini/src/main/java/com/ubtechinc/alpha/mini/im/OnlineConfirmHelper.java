package com.ubtechinc.alpha.mini.im;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.CmConfirmOnline;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * Created by ubt on 2017/12/2.
 */

public class OnlineConfirmHelper {

    private boolean confirming;
    private boolean isStop;

    public void testOnline(final String peer) {
        if (!confirming) {
            confirming = true;
            CmConfirmOnline.CmConfirmOnlineRequest request = CmConfirmOnline.CmConfirmOnlineRequest.getDefaultInstance();
            Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CONFIRM_ONLINE_REQUEST, "1", request, peer, new ICallback<CmConfirmOnline.CmConfirmOnlineResponse>() {
                @Override
                public void onSuccess(CmConfirmOnline.CmConfirmOnlineResponse data) {
                    confirming = false;
                }

                @Override
                public void onError(ThrowableWrapper e) {
                    confirming = false;
                    if (!isStop) {
                        LogUtils.D("testOnline " + e.getErrorCode());
                        MyRobotsLive.getInstance().confirmOffline(peer);
                    }
                    isStop = false;
                }
            });
        }
    }

    public void stop() {
        isStop = true;
    }
}
