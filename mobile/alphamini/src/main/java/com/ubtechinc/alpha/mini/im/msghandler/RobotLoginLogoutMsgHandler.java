package com.ubtechinc.alpha.mini.im.msghandler;

import com.orhanobut.logger.Logger;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.im.msghandler.IMJsonMsgHandler;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.constants.BusinessConstants;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.im.imjsonmsg.IMJsonLoginStateMsg;
import com.ubtechinc.nets.im.modules.IMJsonMsg;
import com.ubtechinc.nets.utils.JsonUtil;

/**
 * Created by Administrator on 2017/6/6.
 */

public class RobotLoginLogoutMsgHandler implements IMJsonMsgHandler {
    private String TAG = "RobotLoginLogoutMsgHandler";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, IMJsonMsg jsonRequest, String peer) {
        String bodyData = jsonRequest.bodyData;
        IMJsonLoginStateMsg loginStateMsg = JsonUtil.getObject(bodyData, IMJsonLoginStateMsg.class);

        if (StringUtils.isEquals(loginStateMsg.state, BusinessConstants.INSTANCE.getROBOT_IM_STATE_CHANGE_LOGOUT())) {
            RobotsViewModel.get().updateRobotOnlineState(loginStateMsg.userId, RobotInfo.ROBOT_STATE_OFFLINE);
        } else if (StringUtils.isEquals(loginStateMsg.state, BusinessConstants.INSTANCE.getROBOT_IM_STATE_CHANGE_LOGIN())) {
            RobotsViewModel.get().updateRobotOnlineState(loginStateMsg.userId, RobotInfo.ROBOT_STATE_ONLINE);
        }
        Logger.t(TAG).d("LoginoutState ------ userId = %s , state = %s", loginStateMsg.userId, loginStateMsg.state);

    }
}
