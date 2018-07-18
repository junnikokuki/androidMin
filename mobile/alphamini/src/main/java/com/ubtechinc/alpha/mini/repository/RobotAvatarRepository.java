package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.AlGetAgoraRoomInfo;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAvatarSource;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

public class RobotAvatarRepository {

    private static RobotAvatarRepository INSTANCE = null;
    private static final String TAG = "RobotAvatarRepository";

    private RobotAvatarRepository() {

    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @return the {@link RobotAvatarRepository} instance
     */
    public static RobotAvatarRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RobotAvatarRepository();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    public void startAvatar(final IRobotAvatarSource.StartAvatarCallback callback) {
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_AGORA_ROOM_INFO_REQUEST, "1",
                null, MyRobotsLive.getInstance().getRobotUserId(), new ICallback<AlGetAgoraRoomInfo.AlGetAgoraRoomInfoResponse>() {
                    @Override
                    public void onSuccess(AlGetAgoraRoomInfo.AlGetAgoraRoomInfoResponse data) {
                        if (callback != null) {
                            if (data.getResultCode() == 0) {
                                callback.onSuccess(data.getRoomNumber(), data.getChannelKey());
                            } else {
                                callback.onFail(new ThrowableWrapper(data.getErrorMsg(), data.getResultCode()));
                            }
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        callback.onFail(e);
                    }
                });
    }
}
