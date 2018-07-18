package com.ubtechinc.alpha.mini.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.MD5Utils;
import com.ubtech.utilcode.utils.TimeUtils;
import com.ubtech.utilcode.utils.thread.HandlerUtils;
import com.ubtechinc.alpha.AIDisConnectRobot;
import com.ubtechinc.alpha.AlConnectRobot;
import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.net.AddQQMusicRecord;
import com.ubtechinc.alpha.mini.net.BindRobotModule;
import com.ubtechinc.alpha.mini.net.CanclePermissionModule;
import com.ubtechinc.alpha.mini.net.ChangeRobotEquipmentIdModule;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.net.GetBindHistoryListModule;
import com.ubtechinc.alpha.mini.net.GetRobotIMStatusModule;
import com.ubtechinc.alpha.mini.net.GetRobotListModule;
import com.ubtechinc.alpha.mini.net.GetShareUrlModule;
import com.ubtechinc.alpha.mini.net.QueryPermissionModule;
import com.ubtechinc.alpha.mini.net.QueryQQMusicRecord;
import com.ubtechinc.alpha.mini.net.RobotSlaveModule;
import com.ubtechinc.alpha.mini.net.UnBindRobotModule;
import com.ubtechinc.alpha.mini.net.UpdatePermissionModule;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAuthorizeDataSource;
import com.ubtechinc.alpha.mini.tvs.TVSManager;
import com.ubtechinc.alpha.mini.utils.ErrorParser;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.im.IMErrorUtil;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtechinc.nets.phonerobotcommunite.ProtoBufferDispose;

import java.util.ArrayList;
import java.util.List;


import static com.ubtechinc.alpha.im.IMCmdId.IM_CHANNEL;


/**
 * @author tanghongyu
 * @ClassName RobotAuthorizeReponsitory
 * @date 6/14/2017
 * @Description 机器人绑定授权相关数据仓库
 * @modifier
 * @modify_time
 */
public class RobotAuthorizeReponsitory implements IRobotAuthorizeDataSource {
    private static final String TAG = "RobotAuthorizeReponsitory";
    private static final String QQMUSIC = "QQMUSIC";
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private static RobotAuthorizeReponsitory INSTANCE = null;
    private ArrayMap<String, Integer> mRobotStateMap = new ArrayMap<>();
    public RobotInfo connectedRobot;
    private ArrayList<RobotInfo> mRobotList = new ArrayList<>();// 包含状态的机器人列表

    private RobotAuthorizeReponsitory() {
    }

    public static RobotAuthorizeReponsitory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RobotAuthorizeReponsitory();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void bindRobot(String robotNo, String userOnlyId, final @NonNull BindRobotCallback callback) {
        BindRobotModule.Request request = new BindRobotModule().new Request();
        request.setUserName(robotNo);
        request.setUserOnlyId(userOnlyId);
        HttpProxy.get().doPost(request, new ResponseListener<BindRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onFail(e);
            }

            @Override
            public void onSuccess(BindRobotModule.Response response) {

                if (ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onSuccess(response.getData().getResult().getMacAddress());
                } else {
                    callback.onFail(ErrorParser.get().getThrowableWrapper());
                }
            }
        });
    }

    @Override
    public void unbindRobot(final String equipmentId, final int userId, final @NonNull UnBindRobotCallback callback) {
        UnBindRobotModule.Request request = new UnBindRobotModule().new Request();
        request.setEquipmentId(equipmentId);
        request.setUserId(userId);
        HttpProxy.get().doPost(request, new ResponseListener<UnBindRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onFail(e);
            }

            @Override
            public void onSuccess(UnBindRobotModule.Response response) {


                if (ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onSuccess();
                } else {
                    callback.onFail(ErrorParser.get().getThrowableWrapper());
                }
            }


        });
    }

    @Override
    public void loadRobotList(final @NonNull LoadRobotListCallback callback) {
        final GetRobotListModule.Request request = new GetRobotListModule().new Request();
        HttpProxy.get().doGet(request, new ResponseListener<GetRobotListModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(GetRobotListModule.Response response) {

                if (ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onLoadRobotList(response.getData().getResult());
                } else {
                    callback.onDataNotAvailable(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }

    @Override
    public void loadHistoryRobotList(String userId, final @NonNull LoadRobotListCallback callback) {
        final GetBindHistoryListModule.Request request = new GetBindHistoryListModule().new Request();
        request.setUserId(userId);
        HttpProxy.get().doPost(request, new ResponseListener<GetBindHistoryListModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(GetBindHistoryListModule.Response response) {

                if (ErrorParser.get().isSuccess(response.getResultCode())) {
                    callback.onLoadRobotList(response.getData().getResult());
                } else {
                    callback.onDataNotAvailable(ErrorParser.get().getThrowableWrapper());
                }

            }
        });
    }

    public void connectRobot(String serialNo, @NonNull final IControlRobotCallback callback) {
        AlConnectRobot.AlConnectRobotRequest.Builder builder = AlConnectRobot.AlConnectRobotRequest.newBuilder();
        builder.setRobotId(serialNo).setUserId(AuthLive.getInstance().getUserId());
        AlConnectRobot.AlConnectRobotRequest request = builder.build();
        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_CONNECT_ROBOT_REQUEST, "1", request, serialNo, new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
            @Override
            public void onSuccess(AlphaMessageOuterClass.AlphaMessage alphaMessage) {
                Logger.d(TAG, "onSuccess---IM 模块 --控制机器人成功");

                if (alphaMessage != null && alphaMessage.getBodyData() != null) {
                    AlConnectRobot.AlConnectRobotResponse connectResponse = (AlConnectRobot.AlConnectRobotResponse) ProtoBufferDispose.unPackData(AlConnectRobot.AlConnectRobotResponse.class, alphaMessage.getBodyData().toByteArray());
                    if (connectResponse.getIsSuccess()) {
                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        });

                    } else {

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail(IMErrorUtil.handleException(0));
                            }
                        });

                    }
                }
            }

            @Override
            public void onError(final ThrowableWrapper e) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e);
                    }
                });
                Logger.d(TAG, "onError---IM 模块 --控制机器人失败 ", e);
            }


        });
    }

    /**
     * 断开与机器人的连接
     */
    public void disConnectRobot(String serialNo, @NonNull final IControlRobotCallback callback) {
        AIDisConnectRobot.AlDisConnectRobotRequest.Builder builder = AIDisConnectRobot.AlDisConnectRobotRequest.newBuilder();
        builder.setRobotId(serialNo).setUserId(AuthLive.getInstance().getUserId());
        AIDisConnectRobot.AlDisConnectRobotRequest request = builder.build();
        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_DISCONNECT_ROBOT_REQUEST, "1", request, serialNo, new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
            @Override
            public void onSuccess(AlphaMessageOuterClass.AlphaMessage alphaMessage) {
                Logger.d(TAG, "onSuccess---IM 模块 --断开与机器人的连接");
                if (alphaMessage != null && alphaMessage.getBodyData() != null) {
                    AIDisConnectRobot.AlDisConnectRobotResponse connectResponse = (AIDisConnectRobot.AlDisConnectRobotResponse) ProtoBufferDispose.unPackData(AIDisConnectRobot.AlDisConnectRobotResponse.class, alphaMessage.getBodyData().toByteArray());
                    if (connectResponse.getIsSuccess()) {
                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        });

                    } else {

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail(IMErrorUtil.handleException(0));
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(final ThrowableWrapper e) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e);
                    }
                });
                Logger.d(TAG, "onError---IM 模块 --断开与机器人的连接 ", e);

            }


        });
    }


    public void getRobotIMStatus(final List<String> robotSNList, final @NonNull GetRobotIMStateCallback callback) {

        Logger.i(TAG, "run getRobotIMStatus");
        long time = TimeUtils.getCurrentTimeInLong();
        String md5 = MD5Utils.md5("IM$SeCrET" + time, 32);
        GetRobotIMStatusModule.Request request = new GetRobotIMStatusModule().new Request();
        StringBuilder buffer = new StringBuilder();
        for (String sn : robotSNList) {
            buffer.append(sn);
            buffer.append(",");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        request.setAccounts(buffer.toString());
        request.setSignature(md5);
        request.setTime(String.valueOf(time));
        request.setUserId(AuthLive.getInstance().getUserId());
        request.setChannel(IM_CHANNEL);
        HttpProxy.get().doGet(request, new ResponseListener<GetRobotIMStatusModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(GetRobotIMStatusModule.Response response) {

                callback.onLoadRobotIMState(response.getReturnMap());

            }
        });
    }

    public void changeRobotEquipmentId(final List<String> robotSNList, final @NonNull GetRobotEquipmentIdCallback callback) {
        Logger.i(TAG, "run getEquipmentId");
        ChangeRobotEquipmentIdModule.Request request = new ChangeRobotEquipmentIdModule().new Request();
        StringBuilder buffer = new StringBuilder();
        for (String sn : robotSNList) {
            buffer.append(sn);
            buffer.append(",");
        }
        request.setSerialNum(buffer.toString());
        HttpProxy.get().doPost(request, new ResponseListener<ChangeRobotEquipmentIdModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Logger.i(TAG, "getEquipmentId fail " + e.getErrorCode() + e.getMessage());
                callback.onDataNotAvailable(e);
            }

            @Override
            public void onSuccess(ChangeRobotEquipmentIdModule.Response response) {
                Logger.i(TAG, "getEquipmentId success" + response.getInfo());
                if (response.isStatus()) {
                    List models = response.getModels();
                    if (!CollectionUtils.isEmpty(models)) {
                        callback.onRobotEquipmentId(response.getModels());
                    } else {
                        callback.onDataNotAvailable(new ThrowableWrapper(response.getInfo(), 500));
                    }
                } else {
                    callback.onDataNotAvailable(new ThrowableWrapper(response.getInfo(), 500));
                }

            }
        });
    }

    public void getSlaveRobotList(String robotUserId, final IGetRobotSlave getRobotSlave) {
        RobotSlaveModule.Request request = new RobotSlaveModule.Request(robotUserId);
        HttpProxy.get().doGet(request, new ResponseListener<RobotSlaveModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                getRobotSlave.onFail(e);
            }

            @Override
            public void onSuccess(RobotSlaveModule.Response response) {
                getRobotSlave.onSuccess(response);
            }
        });
    }

    public void getShareUrl(String robotUserId, boolean isWechat, String permission, final IGetShareUrl getShareUrl) {
        GetShareUrlModule.Request request = new GetShareUrlModule.Request(robotUserId);
        if (isWechat) {
            request.setShareUrlType("WX");
        }
        if (permission != null) {
            request.setPermissions(permission);
        }
        HttpProxy.get().doGet(request, new ResponseListener<GetShareUrlModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                getShareUrl.onFail(e);
            }

            @Override
            public void onSuccess(GetShareUrlModule.Response response) {
                getShareUrl.onSuccess(response);
            }
        });
    }

    public void queryPermission(String slaveUserId, String robotUserId, ResponseListener<QueryPermissionModule.Response> responseListener) {
        QueryPermissionModule.Request request = new QueryPermissionModule.Request(slaveUserId, robotUserId);
        HttpProxy.get().doGet(request, responseListener);
    }

    public void updatePermission(String slaveUserId, String robotUserId, String permission, ResponseListener<UpdatePermissionModule.Response> responseListener) {
        UpdatePermissionModule.Request request = new UpdatePermissionModule.Request(slaveUserId, robotUserId, permission);
        HttpProxy.get().doPost(request, responseListener);
    }

    /**
     * 取消从账号授权
     *
     * @param slaveUserId      从账号ID
     * @param robotUserId      机器人的Id
     * @param masterUserId     指定主账号的Id
     * @param responseListener
     */
    public void canclePermission(String slaveUserId, String robotUserId, ResponseListener<CanclePermissionModule.Response> responseListener) {
        CanclePermissionModule.Request request = new CanclePermissionModule.Request(slaveUserId, robotUserId);
        HttpProxy.get().doPost(request, responseListener);
    }

    /**
     * 移交主账号
     *
     * @param robotUserId
     * @param masterUserId
     * @param responseListener
     */
    public void transferPermission(String robotUserId, String masterUserId, ResponseListener<CanclePermissionModule.Response> responseListener) {
        CanclePermissionModule.Request request = new CanclePermissionModule.Request(masterUserId, robotUserId, null);
        HttpProxy.get().doPost(request, responseListener);
    }

    /**
     * 取消所有绑定账号
     *
     * @param robotUserId
     * @param responseListener
     */
    public void throwPermisson(String robotUserId, ResponseListener<CanclePermissionModule.Response> responseListener) {
        CanclePermissionModule.Request request = new CanclePermissionModule.Request(robotUserId);
        HttpProxy.get().doPost(request, responseListener);
    }


    public void getRobotBindUser(String userId, final ResponseListener<CheckBindRobotModule.Response> listener) {
        final CheckBindRobotModule.Request request = new CheckBindRobotModule.Request();
        request.setRobotUserId(userId);
        HttpProxy.get().doGet(request, new ResponseListener<CheckBindRobotModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(CheckBindRobotModule.Response response) {
                listener.onSuccess(response);
            }
        });

    }

    // 上报领取QQ会员成功
    public void addReceiveSuc(Context context, String robotUserId, String serviceBeginTime, String serviceEndTime, final ResponseListener<AddQQMusicRecord.Response> listener) {
        Log.i(TAG, " addReceiveSuc -- robotUserId : " + robotUserId);
        final AddQQMusicRecord.Request request = new AddQQMusicRecord.Request(robotUserId, serviceBeginTime, serviceEndTime, TVSManager.getInstance(context).getELoginPlatform().equals(ELoginPlatform.WX) ? "WX" : "QQOpen", AuthLive.getInstance().getCurrentUser().getNickName(), QQMUSIC);
        HttpProxy.get().doPost(request, new ResponseListener<AddQQMusicRecord.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.i(TAG, "addReceiveSuc onError -- e: " + Log.getStackTraceString(e));
                listener.onError(e);
            }

            @Override
            public void onSuccess(AddQQMusicRecord.Response response) {
                Log.i(TAG, " onSuccess -- response: " + response);
                listener.onSuccess(response);
            }
        });
    }

    // 查询领取QQ会员状态
    public void getReceiveStatus(@NonNull final ResponseListener<QueryQQMusicRecord.Response> listener) {
        Log.i(TAG, " RobotAuthorizeReponsitory -- getReceiveStatus -- 1");
        QueryQQMusicRecord.Request request = new QueryQQMusicRecord.Request(QQMUSIC);
        HttpProxy.get().doGet(request, new ResponseListener<QueryQQMusicRecord.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.i(TAG, "getReceiveStatus onError -- e: " + Log.getStackTraceString(e));
                listener.onError(e);
            }

            @Override
            public void onSuccess(QueryQQMusicRecord.Response response) {
                Log.i(TAG, " onSuccess -- response: " + response);
                listener.onSuccess(response);
            }
        });
        Log.i(TAG, " RobotAuthorizeReponsitory -- getReceiveStatus -- 2");
    }
}
