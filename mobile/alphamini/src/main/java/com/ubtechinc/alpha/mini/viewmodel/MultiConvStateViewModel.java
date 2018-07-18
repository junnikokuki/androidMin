package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtechinc.alpha.GetMultiConvState;
import com.ubtechinc.alpha.SetMultiConvState;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * @作者：liudongyang
 * @日期: 18/7/14 15:37
 * @描述:
 */
public class MultiConvStateViewModel {

    private static boolean isConvStateOpen;//全局变量是否打开了按钮


    public MultiConvStateViewModel() {

    }


    public LiveResult getConvState() {
        final LiveResult liveResult = new LiveResult();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_MULTI_CONVERSATION_STATE_REQUEST, IMCmdId.IM_VERSION, null, MyRobotsLive.getInstance().getRobotUserId(),
                new ICallback<GetMultiConvState.GetMultiConvStateResponse>() {

                    @Override
                    public void onSuccess(GetMultiConvState.GetMultiConvStateResponse data) {
                        isConvStateOpen = data.getState();
                        liveResult.postSuccess(isConvStateOpen);
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        liveResult.fail(e.getMessage());
                        e.printStackTrace();
                    }
                });
        return liveResult;
    }


    public LiveResult setConvState(final boolean openState) {
        final LiveResult liveResult = new LiveResult();
        SetMultiConvState.SetMultiConvStateRequest.Builder request = SetMultiConvState.SetMultiConvStateRequest.newBuilder();
        request.setState(openState);
        liveResult.loading(openState);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_SET_MULTI_CONVERSATION_STATE_REQUEST, IMCmdId.IM_VERSION,
            request.build(), MyRobotsLive.getInstance().getRobotUserId(), new ICallback<SetMultiConvState.SetMultiConvStateResponse>() {

                @Override
                public void onSuccess(final SetMultiConvState.SetMultiConvStateResponse data) {
                    if (data.getResult()){
                        isConvStateOpen = openState;
                    }else{
                        isConvStateOpen = !openState;
                    }
                    liveResult.postSuccess(isConvStateOpen);
                }

                @Override
                public void onError(ThrowableWrapper e) {
                    isConvStateOpen = !openState;
                    liveResult.fail(e.getMessage());
                    e.printStackTrace();
                }
            });
        return liveResult;
    }

    public static boolean isConvStateOpen() {
        return isConvStateOpen;
    }



}
