package com.ubtechinc.alpha.mini.repository;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.DetectUpgradeProto;
import com.ubtechinc.alpha.DownloadFirmwareProto;
import com.ubtechinc.alpha.FirmwareUpgradeProto;
import com.ubtechinc.alpha.QueryDownloadProgressProto;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

/**
 * Created by ubt on 2018/4/8.
 */

public class UpgradeRepository {

    public static final String TAG = "UpgradeRepository";

    public void checkUpgrade(String robotId, final UpgradeCallback<DetectUpgradeProto.DetectUpgrade> upgradeCallback) {
        LogUtils.d(TAG, "checkUpgrade");
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_DETECT_UPGRADE_REQUEST, IMCmdId.IM_VERSION, null, robotId, new ICallback<DetectUpgradeProto.DetectUpgrade>() {
            @Override
            public void onSuccess(DetectUpgradeProto.DetectUpgrade data) {
                LogUtils.d(TAG, "checkUpgrade onSuccess ", data);
                if (upgradeCallback != null) {
                    if (data != null) {
                        upgradeCallback.onSuccess(data);
                    } else {
                        upgradeCallback.onError(500, data.getErrMsg());
                    }
                }

            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG, "onError", e);
                if (upgradeCallback != null) {
                    upgradeCallback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    public void startDownload(String robotId, final UpgradeCallback upgradeCallback) {
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FIRMWARE_DOWNLOAD_REQUEST, IMCmdId.IM_VERSION, null, robotId, new ICallback<DownloadFirmwareProto.DownloadFirmware>() {
            @Override
            public void onSuccess(DownloadFirmwareProto.DownloadFirmware data) {
                LogUtils.d(TAG, "startDownload onSuccess ", data);
                if (upgradeCallback != null) {
                    upgradeCallback.onSuccess(null);
                }

            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG, "startDownload onError", e);
                if (upgradeCallback != null) {
                    upgradeCallback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

    public void getDownloadProgress(String robotId, final DownloadProgressCallback callback) {
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FIRMWARE_DOWNLOAD_PROGRESS_REQUEST, IMCmdId.IM_VERSION, null, robotId, new ICallback<QueryDownloadProgressProto.QueryDownloadProgress>() {
            @Override
            public void onSuccess(QueryDownloadProgressProto.QueryDownloadProgress data) {
                LogUtils.d(TAG, "getDownloadProgress onSuccess ", data);
                if (callback != null) {
                    QueryDownloadProgressProto.DownloadProgress downloadProgress = data.getProgress();
                    float progress = 0f;
                    if (data.getState() == QueryDownloadProgressProto.DownloadState.DOWNLOADING) {
                        if (downloadProgress.getTotalBytes() != 0) {
                            progress = downloadProgress.getDownloadedBytes() * 100 / downloadProgress.getTotalBytes();
                        }
                    } else if (data.getState() == QueryDownloadProgressProto.DownloadState.COMPLETED) {
                        progress = 100f;
                    } else if (data.getState() == QueryDownloadProgressProto.DownloadState.ERROR || data.getState() == QueryDownloadProgressProto.DownloadState.UNRECOGNIZED) {
                        if (callback != null) {
                            callback.onError(500, data.getErrorMsg());
                        }
                        return;
                    }
                    callback.onProgress((int) progress);
                }

            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG, "getDownloadProgress onErrors", e);
            }
        });
    }

    public void startUpgrade(String robotId, final UpgradeCallback<FirmwareUpgradeProto.UpgradeState> upgradeCallback) {
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FIRMWARE_UPGRADE_REQUEST, IMCmdId.IM_VERSION, null, robotId, new ICallback<FirmwareUpgradeProto.UpgradeResponse>() {
            @Override
            public void onSuccess(FirmwareUpgradeProto.UpgradeResponse data) {
                LogUtils.d(TAG, "startUpgrade onSuccess ", data);
                if (upgradeCallback != null) {
                    FirmwareUpgradeProto.UpgradeState state = data.getState();
                    upgradeCallback.onSuccess(state);
                }

            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG, "startUpgrade onError", e);
                upgradeCallback.onError(e.getErrorCode(), e.getMessage());
            }
        });
    }

    public interface DownloadProgressCallback {

        public void onProgress(int progress);

        public void onError(int code, String msg);
    }

    public interface UpgradeCallback<T> {

        public void onSuccess(T t);

        public void onError(int code, String msg);
    }
}
