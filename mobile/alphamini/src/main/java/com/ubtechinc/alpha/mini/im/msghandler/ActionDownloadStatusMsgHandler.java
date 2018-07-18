package com.ubtechinc.alpha.mini.im.msghandler;

import com.google.protobuf.ByteString;
import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.CmActionFileEntrity;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;


/**
 * @author tanghongyu
 * @ClassName ActionDownloadStatusMsgHandler
 * @date 7/7/2017
 * @Description 动作下载状态回调
 * @modifier
 * @modify_time
 */
public class ActionDownloadStatusMsgHandler implements IMsgHandler {
    private static final String TAG = "ActionDownloadStatusMsg";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        ByteString requestBody = request.getBodyData();
        byte[] bodyBytes = requestBody.toByteArray();
        CmActionFileEntrity.CmActionFileEntrityResponse response = (CmActionFileEntrity.CmActionFileEntrityResponse) ProtoBufferDispose.unPackData(CmActionFileEntrity.CmActionFileEntrityResponse.class, bodyBytes);
        Logger.t(TAG).d("Action download state = %d", response.getDownloadState());
        /*TODO ActionDownloadStatusChangeEvent event = new ActionDownloadStatusChangeEvent();
        ActionFileEntrity actionFileEntrity = new ActionFileEntrity();

        ActionEntrityInfo actionEntrityInfo = ActionInfoProvider.get().findActionByParam(ImmutableMap.of("serialNo", Alpha2Application.getRobotSerialNo(), "actionOriginalID", (Object)response.getActionOriginalId()));
        actionEntrityInfo.setDownloadState(response.getDownloadState());
        ContentValues values = new ContentValues();
        values.put("downloadState", response.getDownloadState());
        ActionInfoProvider.get().updateByParam(values,ImmutableMap.of("serialNo", Alpha2Application.getRobotSerialNo(), "actionOriginalID", (Object)response.getActionOriginalId()) );
        BeanUtils.copyBeanFromProto(response, actionFileEntrity);
        event.setActionFileEntrity(actionFileEntrity);
        EventBus.getDefault().post(event);*/


    }
}
