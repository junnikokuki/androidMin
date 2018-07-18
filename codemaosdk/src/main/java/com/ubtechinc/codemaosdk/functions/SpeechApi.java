package com.ubtechinc.codemaosdk.functions;

import com.ubtechinc.codemao.CodeMaoControlTTS;
import com.ubtechinc.codemao.CodeMaoSwitchTranslateModel;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.interfaces.ISpeech;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/2 16:04
 */

public class SpeechApi {

    private SpeechApi() {}
    private static SpeechApi speechApi = new SpeechApi();
    public static SpeechApi get() {
        if(speechApi == null) {
            synchronized (SpeechApi.class) {
                speechApi = new SpeechApi();
            }

        }
        return speechApi;
    }

    public void playTTS(String text, final ISpeech.PlayTTSCallback playTTSCallback) {
        CodeMaoControlTTS.ControlTTSRequest.Builder builder = CodeMaoControlTTS.ControlTTSRequest.newBuilder();
        builder.setText(text);
        builder.setType(Statics.TTS_PLAY);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_TTS_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoControlTTS.ControlTTSResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlTTS.ControlTTSResponse data) {


                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            playTTSCallback.onCompleted();
                        }else {
                            playTTSCallback.onError(data.getResultCode());
                        }
                    }
                });
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        playTTSCallback.onError(e.getErrorCode());
                    }
                });

            }
        });

    }
    public void stopTTS( final ISpeech.ControlCallback controlCallback) {
        CodeMaoControlTTS.ControlTTSRequest.Builder builder = CodeMaoControlTTS.ControlTTSRequest.newBuilder();
        builder.setType(Statics.TTS_STOP);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_TTS_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoControlTTS.ControlTTSResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlTTS.ControlTTSResponse data) {


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
    public void switchTranslateModel(boolean isOpen, final ISpeech.ControlCallback controlCallback) {
        CodeMaoSwitchTranslateModel.SwitchTranslateModelRequest.Builder builder = CodeMaoSwitchTranslateModel.SwitchTranslateModelRequest.newBuilder();
        builder.setIsOpen(isOpen);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_TRANSLATE_MODEL_SWITCH_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoSwitchTranslateModel.SwitchTranslateModelResponse>() {
            @Override
            public void onSuccess(final CodeMaoSwitchTranslateModel.SwitchTranslateModelResponse data) {


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
}
