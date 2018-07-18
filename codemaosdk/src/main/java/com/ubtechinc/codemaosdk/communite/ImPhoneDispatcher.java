package com.ubtechinc.codemaosdk.communite;


import com.ubtechinc.protocollibrary.annotation.IMJsonMsgRelationVector;
import com.ubtechinc.protocollibrary.annotation.IMMsgRelationVector;
import com.ubtechinc.protocollibrary.annotation.ImMsgRelation;
import com.ubtechinc.protocollibrary.communite.ImMsgDispathcer;
import com.ubtechinc.protocollibrary.protocol.CmdId;

/**
 * Created by Administrator on 2017/5/26.
 */
@IMJsonMsgRelationVector({
})
@IMMsgRelationVector({

        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_BUTTERY_STATUS_RESPONSE, msgHandleClass=ObserveBatteryEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_RACKET_HEAD_RESPONSE, msgHandleClass=ObserveHeadRacketEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_FALL_DOWN_RESPONSE, msgHandleClass=ObserveFallDownEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_ROBOT_POSTURE_RESPONSE, msgHandleClass=ObserveRobotPostureEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_VOLUME_KEY_PRESS_RESPONSE, msgHandleClass=ObserveVolumeKeyPressEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_OBSERVE_DISTANCE_RESPONSE, msgHandleClass=ObserveInfraredDistanceEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_CONTROL_FIND_FACE_RESPONSE, msgHandleClass=FindFaceEventHandler.class),
        @ImMsgRelation(requestCmdId = CmdId.BL_EXIT_CODING_RESPONSE, msgHandleClass=ExitCodingHandler.class),

})
public class
ImPhoneDispatcher extends ImMsgDispathcer {
}

