package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtechinc.alpha.DetectUpgradeProto;
import com.ubtechinc.alpha.FirmwareUpgradeProto;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.repository.UpgradeRepository;

/**
 * Created by ubt on 2018/4/8.
 */

public class UpgradeViewModel {

    UpgradeRepository upgradeRepository = new UpgradeRepository();

    public LiveResult<DetectUpgradeProto.DetectUpgrade> checkUpgrade() {
        final LiveResult liveResult = new LiveResult();
        upgradeRepository.checkUpgrade(MyRobotsLive.getInstance().getRobotUserId(), new UpgradeRepository.UpgradeCallback<DetectUpgradeProto.DetectUpgrade>() {
            @Override
            public void onSuccess(DetectUpgradeProto.DetectUpgrade info) {
                liveResult.postSuccess(info);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }


    public LiveResult startDownload() {
        final LiveResult liveResult = new LiveResult();
        upgradeRepository.startDownload(MyRobotsLive.getInstance().getRobotUserId(), new UpgradeRepository.UpgradeCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                liveResult.postSuccess(null);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }


    public LiveResult getDownloadProgress() {
        final LiveResult<Integer> liveResult = new LiveResult();
        upgradeRepository.getDownloadProgress(MyRobotsLive.getInstance().getRobotUserId(), new UpgradeRepository.DownloadProgressCallback() {

            @Override
            public void onProgress(int progress) {
                liveResult.success(progress);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }

    public LiveResult<FirmwareUpgradeProto.UpgradeState> startUpgrade() {
        final LiveResult<FirmwareUpgradeProto.UpgradeState> liveResult = new LiveResult();
        upgradeRepository.startUpgrade(MyRobotsLive.getInstance().getRobotUserId(), new UpgradeRepository.UpgradeCallback<FirmwareUpgradeProto.UpgradeState>() {
            @Override
            public void onSuccess(FirmwareUpgradeProto.UpgradeState state) {
                liveResult.postSuccess(state);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }
}
