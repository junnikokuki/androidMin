package com.ubtechinc.codemaosdk.functions;

import com.ubtechinc.codemao.CodeMaoControlMouthLamp;
import com.ubtechinc.codemao.CodeMaoSetMouthLamp;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.interfaces.ILamp;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

import java.util.ArrayList;
import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/3 9:10
 */

public class LampApi {
    private LampApi() {}
    private static LampApi lampApi = new LampApi();
    public static LampApi get() {
        if(lampApi == null) {
            synchronized (LampApi.class) {
                lampApi = new LampApi();
            }

        }
        return lampApi;
    }

    public void controlMouthLamp(boolean isOpen, final ILamp.ControlCallback controlCallback) {
        CodeMaoControlMouthLamp.ControlMouthRequest.Builder builder = CodeMaoControlMouthLamp.ControlMouthRequest.newBuilder();
        builder.setIsOpen(isOpen);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_MOUTH_LAMP_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoControlMouthLamp.ControlMouthResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlMouthLamp.ControlMouthResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getIsSuccess()) {
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
    public void startMouthBreathLamp(int color,int breathDuration, int duration, final ILamp.ControlCallback controlCallback) {
        CodeMaoSetMouthLamp.SetMouthLampRequest.Builder builder = CodeMaoSetMouthLamp.SetMouthLampRequest.newBuilder();
        builder.setColor(color);
        builder.setModel(Statics.MOUTH_LAMP_MODLE_BREATH);
        builder.setDuration(duration);
        builder.setBreathDuration(breathDuration);

        Phone2RobotMsgMgr.get().sendData(CmdId.BL_SET_MOUTH_LAMP_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoSetMouthLamp.SetMouthLampResponse>() {
            @Override
            public void onSuccess(final CodeMaoSetMouthLamp.SetMouthLampResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getIsSuccess()) {
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
    public void startMouthNormalLamp( int color, int duration, final ILamp.ControlCallback controlCallback) {
        CodeMaoSetMouthLamp.SetMouthLampRequest.Builder builder = CodeMaoSetMouthLamp.SetMouthLampRequest.newBuilder();
        builder.setColor(color);
        builder.setModel(Statics.MOUTH_LAMP_MODLE_NORMAL);
        builder.setDuration(duration);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_SET_MOUTH_LAMP_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoSetMouthLamp.SetMouthLampResponse>() {
            @Override
            public void onSuccess(final CodeMaoSetMouthLamp.SetMouthLampResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getIsSuccess()) {
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

}
