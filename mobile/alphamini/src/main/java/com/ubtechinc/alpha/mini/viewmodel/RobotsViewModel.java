package com.ubtechinc.alpha.mini.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.constants.BusinessConstants;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.SimState;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.CanclePermissionModule;
import com.ubtechinc.alpha.mini.net.ChangeRobotEquipmentIdModule;
import com.ubtechinc.alpha.mini.net.QueryPermissionModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.repository.SimRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAuthorizeDataSource;
import com.ubtechinc.alpha.mini.support.tvs.RobotTvsManagerImpl;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.viewmodel.unbind.UnbindDialog;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RobotsViewModel {

    public static final String TAG = "RobotsViewModel";

    private String lastConnectedRobotId;

    private SimRepository simRepository;

    private static class RobotsViewModelHolder {
        private static RobotsViewModel instance = new RobotsViewModel();
    }

    private RobotsViewModel() {
        simRepository = new SimRepository();
    }

    public static RobotsViewModel get() {
        return RobotsViewModelHolder.instance;
    }

    public void getMyRobots() {
        getMyRobots(0);
    }

    private void getMyRobots(final int count) {
        RobotAuthorizeReponsitory.getInstance().loadRobotList(new IRobotAuthorizeDataSource.LoadRobotListCallback() {
            @Override
            public void onLoadRobotList(List<RobotInfo> robotList) {
//                MyRobotsLive.getInstance().updateList(robotList);
                getRobotImState(robotList);
            }

            @Override
            public void onDataNotAvailable(ThrowableWrapper e) {
                if (count < 3) {
                    getMyRobots(count + 1);
                } else {
                    if (CollectionUtils.isEmpty(MyRobotsLive.getInstance().getRobots().getData())) {
                        MyRobotsLive.getInstance().getRobots().fail("");
                    }
                }
            }
        });
    }

    public LiveResult<RobotInfo> connectToRobot(final RobotInfo robotInfo) {
        final LiveResult<RobotInfo> result = new LiveResult<>();
        boolean isMaster = robotInfo.isMaster();
        if (!isMaster && CollectionUtils.isEmpty(robotInfo.getRobotPermissionList())) {
            RobotAuthorizeReponsitory.getInstance().queryPermission(null, robotInfo.getRobotUserId(), new ResponseListener<QueryPermissionModule.Response>() {
                @Override
                public void onError(ThrowableWrapper e) {
                    result.fail("");
                }

                @Override
                public void onSuccess(QueryPermissionModule.Response response) {
                    MyRobotsLive.getInstance().updatePermission(robotInfo.getRobotUserId(), response.getData().getResult());
                    doConnect2Robot(robotInfo, result);
                }
            });
        } else {
            doConnect2Robot(robotInfo, result);
        }
        return result;
    }

    private void doConnect2Robot(final RobotInfo robotInfo, final LiveResult<RobotInfo> result) {
        MyRobotsLive.getInstance().clearSelectToRobot();
        disconnectRobot(robotInfo.getRobotUserId());
        if (robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            RobotAuthorizeReponsitory.getInstance().connectRobot(robotInfo.getRobotUserId(), new IRobotAuthorizeDataSource.IControlRobotCallback() {
                @Override
                public void onSuccess() {
                    lastConnectedRobotId = robotInfo.getRobotUserId();
                    result.success(robotInfo);
                    MyRobotsLive.getInstance().postUpdateRobotState(robotInfo.getRobotUserId(), RobotInfo.ROBOT_STATE_ONLINE);
                    if (robotInfo.isMaster()) {
                        TVSManager.getInstance(AlphaMiniApplication.getInstance()).tvsAuth(RobotTvsManagerImpl.getInstance(), new TVSManager.TVSAuthListener() {
                            @Override
                            public void onSuccess(String clientId) {

                            }

                            @Override
                            public void onError(int code) {
                                if (code == 403) {
                                    AuthLive.getInstance().forbidden();
                                }
                            }

                        });
                        TVSManager.getInstance(AlphaMiniApplication.getInstance()).bindRobot(robotInfo.getRobotUserId());
                    }
                }

                @Override
                public void onFail(ThrowableWrapper e) {
                    result.fail("");
                }
            });
        } else {
            result.fail("");
        }
    }

    public LiveResult<RobotInfo> switchToRobot(final RobotInfo robotInfo) {
        final LiveResult<RobotInfo> result = new LiveResult<>();
        disconnectRobot(robotInfo.getRobotUserId());
        boolean isMaster = robotInfo.isMaster();
        if (!isMaster && CollectionUtils.isEmpty(robotInfo.getRobotPermissionList())) {
            RobotAuthorizeReponsitory.getInstance().queryPermission(null, robotInfo.getRobotUserId(), new ResponseListener<QueryPermissionModule.Response>() {

                @Override
                public void onError(ThrowableWrapper e) {
                    MyRobotsLive.getInstance().selectRobot(robotInfo.getRobotUserId());
                    result.fail("");
                }

                @Override
                public void onSuccess(QueryPermissionModule.Response response) {
                    MyRobotsLive.getInstance().updatePermission(robotInfo.getRobotUserId(), response.getData().getResult());
                    MyRobotsLive.getInstance().selectRobot(robotInfo.getRobotUserId());
                    result.success(robotInfo);
                }
            });
        } else {
            MyRobotsLive.getInstance().selectRobot(robotInfo.getRobotUserId());
            result.success(robotInfo);
        }
        return result;
    }

    public LiveResult<RobotInfo> reConnectRobot(final RobotInfo robotInfo) {
        final LiveResult<RobotInfo> liveResult = new LiveResult<>();
        List<String> robotSNList = Arrays.asList(robotInfo.getRobotUserId());
        RobotAuthorizeReponsitory.getInstance().getRobotIMStatus(robotSNList, new IRobotAuthorizeDataSource.GetRobotIMStateCallback() {
            @Override
            public void onLoadRobotIMState(Map<String, String> stateMap) {
                String equipmentId = robotInfo.getRobotUserId();
                String state = stateMap.get(equipmentId);
                if (StringUtils.isEquals(BusinessConstants.INSTANCE.getROBOT_IM_STATE_ONLINE(), state)) {
                    robotInfo.setOnlineState(RobotInfo.ROBOT_STATE_ONLINE);
                    doConnect2Robot(robotInfo, liveResult);
                } else {
                    robotInfo.setOnlineState(RobotInfo.ROBOT_STATE_OFFLINE);
                    liveResult.fail("");
                }
            }

            @Override
            public void onDataNotAvailable(ThrowableWrapper e) {
                liveResult.fail("");
            }
        });
        return liveResult;
    }

    private void checkRobotImState(RobotInfo robotInfo, IRobotAuthorizeDataSource.GetRobotIMStateCallback callback) {
        List<String> robotSNList = Arrays.asList(robotInfo.getRobotUserId());
        RobotAuthorizeReponsitory.getInstance().getRobotIMStatus(robotSNList, callback);
    }

    public void getRobotImState(final List<RobotInfo> robotList) {
        List<String> robotSNList = getRobotUserIds(robotList);
        if (CollectionUtils.isEmpty(robotSNList)) {
            MyRobotsLive.getInstance().updateList(robotList);
            return;
        }
        RobotAuthorizeReponsitory.getInstance().getRobotIMStatus(robotSNList, new IRobotAuthorizeDataSource.GetRobotIMStateCallback() {
            @Override
            public void onLoadRobotIMState(Map<String, String> stateMap) {
                for (RobotInfo robotInfo : robotList) {
                    String equipmentId = robotInfo.getRobotUserId();
                    String state = stateMap.get(equipmentId);
                    if (StringUtils.isEquals(BusinessConstants.INSTANCE.getROBOT_IM_STATE_ONLINE(), state)) {
                        robotInfo.setOnlineState(RobotInfo.ROBOT_STATE_ONLINE);
                    } else {
                        robotInfo.setOnlineState(RobotInfo.ROBOT_STATE_OFFLINE);
                    }
                }
                getRobotEquipmentId(robotList);
            }

            @Override
            public void onDataNotAvailable(ThrowableWrapper e) {
                getRobotEquipmentId(robotList);
            }
        });
    }

    public void getRobotEquipmentId(final List<RobotInfo> robotList) {
//        final List<RobotInfo> robotList = MyRobotsLive.getInstance().getRobots().getData();
        if (CollectionUtils.isEmpty(robotList)) {
            MyRobotsLive.getInstance().updateList(robotList);
            return;
        }
        List<String> robotIds = new ArrayList<>();
        for (final RobotInfo robotInfo : robotList) {
            if (robotInfo.isMaster() && TextUtils.isEmpty(robotInfo.getEquipmentSeq())) {
                String equipmentId = SPUtils.get().getString(robotInfo.getRobotUserId() + "_equipmentId", null);
                if (TextUtils.isEmpty(equipmentId)) {
                    Log.d(TAG, "need to change equipmentId: " + robotInfo.getRobotUserId());
                    robotIds.add(robotInfo.getRobotUserId());
                } else {
                    robotInfo.setEquipmentSeq(equipmentId);
                }
            }
        }
        if (CollectionUtils.isEmpty(robotIds)) {
            Log.d(TAG, "all robot is changed equipmentId");
            MyRobotsLive.getInstance().updateList(robotList);
            return;
        }
        RobotAuthorizeReponsitory.getInstance().changeRobotEquipmentId(robotIds, new IRobotAuthorizeDataSource.GetRobotEquipmentIdCallback() {
            @Override
            public void onRobotEquipmentId(List<ChangeRobotEquipmentIdModule.Model> models) {
                for (ChangeRobotEquipmentIdModule.Model model : models) {
                    for (RobotInfo robotInfo : robotList) {
                        if (robotInfo.getRobotUserId().equals(model.getSerialNum())) {
                            robotInfo.setEquipmentSeq(model.getSeqNum());
                            SPUtils.get().put(robotInfo.getRobotUserId() + "_equipmentId", model.getSeqNum());
                        }
                    }
                }
                MyRobotsLive.getInstance().updateList(robotList);
            }

            @Override
            public void onDataNotAvailable(ThrowableWrapper e) {
                MyRobotsLive.getInstance().updateList(robotList);
            }
        });
    }

    public void doThrowRobotPermission(final UnbindDialog.IOnUnbindListener listener, final String robotUserId) {
        if (TextUtils.isEmpty(robotUserId)) {
            Log.e(TAG, "doThrowRobotPermission: " + robotUserId);
            return;
        }
        RobotAuthorizeReponsitory.getInstance().throwPermisson(robotUserId, new ResponseListener<CanclePermissionModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                onErrorNotify();
            }

            private void onErrorNotify() {
                if (listener != null) {
                    listener.onUnbindFailed();
                }
                ToastUtils.showShortToast("解绑失败");
            }

            @Override
            public void onSuccess(CanclePermissionModule.Response response) {
                if (response.getResultCode() == 200) {
                    RobotInfo robotInfo = MyRobotsLive.getInstance().getRobotById(robotUserId);
                    MyRobotsLive.getInstance().remove(robotUserId);
                    if (listener != null) {
                        listener.onUnbindSuccess();
                    }
                    if (robotInfo != null && robotInfo.isMaster()) {
                        TVSManager.getInstance(AlphaMiniApplication.getInstance()).unbindRobot(robotUserId);
                    }
                    ToastUtils.showShortToast("解绑成功");
                } else {
                    onErrorNotify();
                }
            }
        });


    }

    public void doThrowCurrentPermission(final UnbindDialog.IOnUnbindListener listener) {
        final String robotUserId = MyRobotsLive.getInstance().getRobotUserId();
        if (TextUtils.isEmpty(robotUserId)) {
            return;
        }
        RobotAuthorizeReponsitory.getInstance().throwPermisson(robotUserId, new ResponseListener<CanclePermissionModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                onErrorNotify();
            }

            private void onErrorNotify() {
                if (listener != null) {
                    listener.onUnbindFailed();
                }
                ToastUtils.showShortToast("解绑失败");
            }

            @Override
            public void onSuccess(CanclePermissionModule.Response response) {
                if (response.getResultCode() == 200) {
                    RobotInfo robotInfo = MyRobotsLive.getInstance().getRobotById(robotUserId);
                    MyRobotsLive.getInstance().remove(robotUserId);
                    if (listener != null) {
                        listener.onUnbindSuccess();
                    }
                    if (robotInfo != null && robotInfo.isMaster()) {
                        TVSManager.getInstance(AlphaMiniApplication.getInstance()).unbindRobot(robotUserId);
                    }
                    ToastUtils.showShortToast("解绑成功");
                } else {
                    onErrorNotify();
                }
            }
        });
    }

    public void cancelPermission(String mSlaverUserId, final UnbindDialog.IOnUnbindListener listener) {
        RobotAuthorizeReponsitory.getInstance().canclePermission(mSlaverUserId, MyRobotsLive.getInstance().getRobotUserId(), new ResponseListener<CanclePermissionModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                if (listener != null)
                    listener.onUnbindFailed();
            }

            private void onErrorNotify() {
                if (listener != null) {
                    listener.onUnbindFailed();
                }
            }

            @Override
            public void onSuccess(CanclePermissionModule.Response response) {
                if (response.getResultCode() == 200) {
                    if (listener != null)
                        listener.onUnbindSuccess();
                } else {
                    onErrorNotify();
                }
            }
        });
    }

    public void transferPermission(final String robotUserId, String masterId, final UnbindDialog.IOnUnbindListener listener) {
        RobotAuthorizeReponsitory.getInstance().transferPermission(robotUserId, masterId, new ResponseListener<CanclePermissionModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                onErrorNotify();
            }

            private void onErrorNotify() {
                if (listener != null) {
                    listener.onUnbindFailed();
                }
            }

            @Override
            public void onSuccess(CanclePermissionModule.Response response) {
                if (response.getResultCode() == 200) {
                    RobotInfo robotInfo = MyRobotsLive.getInstance().getRobotById(robotUserId);
                    if (listener != null)
                        listener.onUnbindSuccess();
                    MyRobotsLive.getInstance().remove(robotUserId);
                    if (robotInfo != null && robotInfo.isMaster()) {
                        TVSManager.getInstance(AlphaMiniApplication.getInstance()).unbindRobot(robotUserId);
                    }
                } else {
                    onErrorNotify();
                }
            }
        });
    }

    private List<String> getRobotUserIds(List<RobotInfo> robotList) {
        if (!CollectionUtils.isEmpty(robotList)) {
            List<String> sns = new ArrayList<>(robotList.size());
            for (RobotInfo robotInfo : robotList) {
                sns.add(robotInfo.getRobotUserId());
            }
            return sns;
        }
        return Collections.emptyList();
    }

    public void updateRobotOnlineState(String robotUserId, int onlineState) {
        Log.d(TAG, "updateRobotOnlineState " + robotUserId + " onlineState = " + onlineState);
        if (robotUserId != null) {
            if (robotUserId.equals(MyRobotsLive.getInstance().getRobotUserId())) {
                switch (onlineState) {
                    case RobotInfo.ROBOT_STATE_UNAVAILABLE:
                    case RobotInfo.ROBOT_STATE_OFFLINE:
                        MyRobotsLive.getInstance().postUpdateRobotState(robotUserId, onlineState);
                        break;
                    case RobotInfo.ROBOT_STATE_ONLINE:
                        MyRobotsLive.getInstance().postUpdateRobotState(robotUserId, onlineState);
                        break;
                }
            } else {
                MyRobotsLive.getInstance().postUpdateRobotState(robotUserId, onlineState);
            }

        }
    }

    private void disconnectRobot(String newRobotId) {
        if (!TextUtils.isEmpty(lastConnectedRobotId) && !lastConnectedRobotId.equals(newRobotId)) {
            RobotAuthorizeReponsitory.getInstance().disConnectRobot(lastConnectedRobotId, new IRobotAuthorizeDataSource.IControlRobotCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(ThrowableWrapper e) {

                }
            });
        }
        lastConnectedRobotId = null;
    }

    public LiveResult<SimState> checkSimState() {
        final LiveResult liveResult = new LiveResult();
        simRepository.checkSimState(MyRobotsLive.getInstance().getRobotUserId(), new SimRepository.CheckSimStateCallback() {
            @Override
            public void onSuccess(SimState simState) {
                liveResult.success(simState);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }
}
