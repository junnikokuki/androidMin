package com.ubtechinc.alpha.mini.im.msghandler;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.msghandler.IMsgHandler;


/**
 * @author tanghongyu
 * @ClassName AppDownloadStatusMsgHandler
 * @date 7/7/2017
 * @Description App下载状态回调
 * @modifier
 * @modify_time
 */
public class AppDownloadStatusMsgHandler implements IMsgHandler {
    private static final String TAG = "AppDownloadStatusMsgHan";

    @Override
    public void handleMsg(int requestCmdId, int responseCmdId, AlphaMessageOuterClass.AlphaMessage request, String peer) {
        /* TODO ByteString requestBody = request.getBodyData();
        byte[] bodyBytes = requestBody.toByteArray();
        CmAppInstallState.CmAppInstallStateResponse appEntrityInfo = (CmAppInstallState.CmAppInstallStateResponse) ProtoBufferDispose.unPackData(CmAppInstallState.CmAppInstallStateResponse.class,bodyBytes);
        Logger.t(TAG).d("App install state = %d" , appEntrityInfo.getState());

        AppDownloadStatusChangeEvent event = new AppDownloadStatusChangeEvent();
        RobotApp app = new RobotApp();
        app.setAppKey(appEntrityInfo.getAppId());
        app.setDownloadState(appEntrityInfo.getState());
        app.setPackageName(appEntrityInfo.getPackageName());
        event.setAppEntrityInfo(app);
        RobotAppEntrity robotAppEntrity =AppInfoProvider.get().findAppByParam(ImmutableMap.of("serialNo", Alpha2Application.getRobotSerialNo(), "packageName", (Object)app.getPackageName()));
        if(robotAppEntrity != null) {
            robotAppEntrity.setDownloadState(appEntrityInfo.getState());

            AppInfoProvider.get().updateValuesByParam(robotAppEntrity,ImmutableMap.of("serialNo", Alpha2Application.getRobotSerialNo(), "packageName", (Object)app.getPackageName()), "downloadState" );
            EventBus.getDefault().post(event);
        }
*/
    }
}
