package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtechinc.alpha.CmCameraPrivacy;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * @作者：liudongyang
 * @日期: 18/7/16 13:49
 * @描述:
 */
public class CameraPrivacyViewModel {

    private static boolean isPrivaceEnable;

    public CameraPrivacyViewModel() {

    }


    public LiveResult getComunicationState(){
        final LiveResult liveResult = new LiveResult();
        CmCameraPrivacy.CmCameraPrivacyRequest.Builder builder = CmCameraPrivacy.CmCameraPrivacyRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_CAMREA_PRIVACY_REQUEST, IMCmdId.IM_VERSION,
                builder.build(), MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmCameraPrivacy.CmCameraPrivacyResponse>(){

                    @Override
                    public void onSuccess(CmCameraPrivacy.CmCameraPrivacyResponse data) {
                        liveResult.postSuccess(data.getType());
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        liveResult.fail(e.getMessage());
                    }
                });

        return liveResult;
    }

    public LiveResult setComunicationState(final boolean enable){
        final LiveResult liveResult = new LiveResult();
        CmCameraPrivacy.CmCameraPrivacyRequest.Builder builder = CmCameraPrivacy.CmCameraPrivacyRequest.newBuilder();
        builder.setTypeValue(enable ? CmCameraPrivacy.CameraPrivacyType.ON_VALUE : CmCameraPrivacy.CameraPrivacyType.OFF_VALUE);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_SET_CAMREA_PRIVACY_REQUEST, IMCmdId.IM_VERSION,builder.build()
            ,MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmCameraPrivacy.CmCameraPrivacyResponse>(){

                    @Override
                    public void onSuccess(CmCameraPrivacy.CmCameraPrivacyResponse data) {
                        if (data.getType().getNumber() == CmCameraPrivacy.CameraPrivacyType.ON_VALUE){
                            isPrivaceEnable = true;
                        }else{
                            isPrivaceEnable = false;
                        }
                        liveResult.postSuccess(data);
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        liveResult.fail(e.getMessage());
                    }
                });
        return liveResult;
    }

    public static boolean isEnable(){
        return isPrivaceEnable;
    }

}
