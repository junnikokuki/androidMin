package com.ubtechinc.alpha.mini.im;

import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.im.ImMsgDispathcer;
import com.ubtechinc.alpha.mini.im.msghandler.AccountApplyMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.ActionDownloadStatusMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.AppDownloadStatusMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.AvatarLowPowerOf30MsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.AvatarRobotFailDownHandler;
import com.ubtechinc.alpha.mini.im.msghandler.AvatarStoppedMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.AvatarUserChangeMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JiMuCarPowerMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarCarListMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarCheckMagHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarIrDinstanceMagHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarPreparedMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarQueryConnectMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.JimuCarDriverModeMsgHandler;
import com.ubtechinc.alpha.mini.im.msghandler.RobotLoginLogoutMsgHandler;
import com.ubtechinc.nets.im.annotation.IMJsonMsgRelationVector;
import com.ubtechinc.nets.im.annotation.IMMsgRelationVector;
import com.ubtechinc.nets.im.annotation.ImJsonMsgRelation;
import com.ubtechinc.nets.im.annotation.ImMsgRelation;

/**
 * Created by Administrator on 2017/5/26.
 */
@IMJsonMsgRelationVector({@ImJsonMsgRelation(requestCmdId = IMCmdId.IM_OFFLINE_FROM_SERVER_RESPONSE, msgHandleClass = RobotLoginLogoutMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_APPLY_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_HANDLE_APPLY_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_BEEN_UNBIND_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_MASTER_UNBINDED_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_PERMISSION_CHANGE_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_SLAVER_UNBIND_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class),
        @ImJsonMsgRelation(requestCmdId = IMCmdId.IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE, msgHandleClass = AccountApplyMsgHandler.class)
})
@IMMsgRelationVector({@ImMsgRelation(requestCmdId = IMCmdId.IM_APP_INSTALL_STATE_RESPONSE, msgHandleClass = AppDownloadStatusMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_ACTIONFILE_DOWNLOAD_STATE_RESPONSE, msgHandleClass = ActionDownloadStatusMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_AVATAR_STOPPED_RESPONSE, msgHandleClass = AvatarStoppedMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_AVATAR_FAIL_RESPONSE, msgHandleClass = AvatarRobotFailDownHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_AVATAR_USER_CHANGE_RESPONSE, msgHandleClass = AvatarUserChangeMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_AVATAR_LOWPOWER_TIPS_RESPONSE, msgHandleClass = AvatarLowPowerOf30MsgHandler.class),

        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_QUERY_POWER_RESPONSE, msgHandleClass = JiMuCarPowerMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_CHANGE_DRIVE_MODE_RESPONSE, msgHandleClass = JimuCarDriverModeMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_QUERY_CONNECT_STATE_RESPONSE, msgHandleClass = JimuCarQueryConnectMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_CHECK_RESPONSE, msgHandleClass = JimuCarCheckMagHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_GET_IR_DISTANCE_RESPONSE, msgHandleClass = JimuCarIrDinstanceMagHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_GET_BLE_CAR_LIST_RESPONSE, msgHandleClass = JimuCarCarListMsgHandler.class),
        @ImMsgRelation(requestCmdId = IMCmdId.IM_JIMU_CAR_CHECK_PREPARED_RESPONSE, msgHandleClass = JimuCarPreparedMsgHandler.class),
})

public class
ImPhoneClientMsgDispatcher extends ImMsgDispathcer {


}

