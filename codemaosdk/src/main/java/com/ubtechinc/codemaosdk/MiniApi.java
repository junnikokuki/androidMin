package com.ubtechinc.codemaosdk;

import android.app.Application;
import android.content.Context;

import com.clj.fastble.IConnectionControl;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.server.HeartBeatStrategy;
import com.ubtechinc.codemao.CodeMaoControlRunningProgram;
import com.ubtechinc.codemao.CodeMaoHandShake;
import com.ubtechinc.codemao.CodeMaoRevertOrigin;
import com.ubtechinc.codemaosdk.bean.UbtBleDevice;
import com.ubtechinc.codemaosdk.business.ConnectionManager;
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

import org.jetbrains.annotations.NotNull;


/**
* @Description mini控制API
* @Author tanghongyu
* @Time  2018/3/15 14:22
*/
public class MiniApi {
    private static final String TAG = "MiniApi";
    private static MiniApi miniApi;
    private Context mContext;

    public void init(Application context) {
        this.mContext = context;
        ConnectionManager.get().init(context);
        HeartBeatStrategy.Companion.getInstance().init(new IConnectionControl() {
            @Override
            public void disconnect() {
//                disconnectRobot();
            }

            @Override
            public void sendHeartBeat(@NotNull IHeartCallback iHeartCallback) {
                heartbeat(iHeartCallback);
            }
        });
    }



    public static MiniApi get() {
        if (miniApi == null) {
            synchronized (MiniApi.class) {
                if (miniApi == null) {
                    miniApi = new MiniApi();
                }
            }
        }
        return miniApi;
    }


    public void startScanRobot(final IRobotBleControl.ScanResultCallback scanResultCallback) {

       startScanRobot(null, scanResultCallback);
    }
    public void startScanRobot(BleScanRuleConfig scanRuleConfig,final IRobotBleControl.ScanResultCallback scanResultCallback) {
        ConnectionManager.get().stopScanRobot();
        if (mContext == null) {
            throw new IllegalArgumentException("MiniApi must be initialized first");
        }
        ConnectionManager.get().scanRobot(scanRuleConfig, scanResultCallback);


    }

    public void connectRobot(UbtBleDevice connectDevice, final IRobotBleControl.ConnectingResultCallback iConnectResultCallback) {
        if (mContext == null) {
            throw new IllegalArgumentException("MiniApi must be initialized first");
        }
        ConnectionManager.get().connectRobot(connectDevice);
        ConnectionManager.get().registerRobotStateListener(iConnectResultCallback);
    }

    public void registerRobotBleStateListener(final IRobotBleControl.ConnectingResultCallback iConnectResultCallback) {
        ConnectionManager.get().registerRobotStateListener(iConnectResultCallback);

    }
    public void unRegisterRobotBleStateListener(final IRobotBleControl.ConnectingResultCallback iConnectResultCallback) {
        ConnectionManager.get().unregisterRobotStateListener(iConnectResultCallback);

    }





    public void disconnectRobot() {
        ConnectionManager.get().disconnectRobot();
    }

    public void stopScanRobot() {
        if (mContext == null) {
            throw new IllegalArgumentException("MiniApi must be initialized first");
        }
        ConnectionManager.get().stopScanRobot();
    }

    public boolean isRobotConnected() {


        return ConnectionManager.get().isConnected();
    }

    public UbtBleDevice getConnectedRobot() {
        return ConnectionManager.get().getConnectedRobot();
    }
    public void revertOrigin(final IRobotBleControl.ControlCallback controlCallback) {

            Phone2RobotMsgMgr.get().sendData(CmdId.BL_REVERT_ORIGINAL_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoRevertOrigin.RevertOriginResponse>() {
                @Override
                public void onSuccess(final CodeMaoRevertOrigin.RevertOriginResponse data) {


                    HandlerUtils.runUITask(new Runnable() {
                        @Override
                        public void run() {
                            if(data.getIsSuccess()) {
                                controlCallback.onSuccess();
                            }else {
                                controlCallback.onError(data.getResultCode());
                            }
                        }
                    });
                }

                @Override
                public void onError(final ThrowableWrapper e) {
                    HandlerUtils.runUITask(new Runnable() {
                        @Override
                        public void run() {
                            controlCallback.onError(e.getErrorCode());
                        }
                    });

                }
            });

    }
    private void heartbeat(final IConnectionControl.IHeartCallback iHeartCallback) {

        Phone2RobotMsgMgr.get().sendHeartData(CmdId.BL_HEART_BEAT_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoRevertOrigin.RevertOriginResponse>() {
            @Override
            public void onSuccess(final CodeMaoRevertOrigin.RevertOriginResponse data) {


                iHeartCallback.onSuccess();
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                iHeartCallback.onFail();

            }
        });

    }
    public void handshake(final IConnectionControl.IControlCallback IControlCallback) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_HAND_SHAKE_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoHandShake.HandShakeResponse>() {
            @Override
            public void onSuccess(final CodeMaoHandShake.HandShakeResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            IControlCallback.onSuccess();
                        }else {
                            IControlCallback.onFail(data.getResultCode());
                        }
                    }
                });


            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        IControlCallback.onFail(e.getErrorCode());
                    }
                });


            }
        });

    }

    public void controlRunningProgram(boolean isStart,final IConnectionControl.IControlCallback IControlCallback) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_RUNNING_PROGRAM_REQUEST, Statics.PROTO_VERSION, CodeMaoControlRunningProgram.ControlRunningProgramRequest.newBuilder().setIsStart(isStart).build(), "", new ICallback<CodeMaoControlRunningProgram.ControlRunningProgramResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlRunningProgram.ControlRunningProgramResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            IControlCallback.onSuccess();
                        }else {
                            IControlCallback.onFail(data.getResultCode());
                        }
                    }
                });


            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        IControlCallback.onFail(e.getErrorCode());
                    }
                });


            }
        });

    }

    public void destory() {

    }

}
