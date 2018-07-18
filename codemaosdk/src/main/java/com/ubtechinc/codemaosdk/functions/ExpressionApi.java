package com.ubtechinc.codemaosdk.functions;

import com.ubtechinc.codemao.CodeMaoControlBehavior;
import com.ubtechinc.codemao.CodeMaoExpressionInfo;
import com.ubtechinc.codemao.CodeMaoGetRobotExpression;
import com.ubtechinc.codemao.CodeMaoPlayExpression;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.bean.Expression;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.interfaces.IExpression;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

import java.util.ArrayList;
import java.util.List;

/**
 * @Deseription 表情相关API
 * @Author tanghongyu
 * @Time 2018/4/3 9:19
 */

public class ExpressionApi {

    private ExpressionApi() {}
    private static ExpressionApi sysApi = new ExpressionApi();
    public static ExpressionApi get() {
        if(sysApi == null) {
            synchronized (ExpressionApi.class) {
                sysApi = new ExpressionApi();
            }

        }
        return sysApi;
    }

    public void getExpressList(final IExpression.GetExpressionList getExpressionListCallback) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_EXPRESS_LIST_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoGetRobotExpression.GetRobotExpressionResponse>() {
            @Override
            public void onSuccess(final CodeMaoGetRobotExpression.GetRobotExpressionResponse data) {
                final List<Expression> expressionList = new ArrayList<>();
                for ( CodeMaoExpressionInfo.ExpressionInfoResponse response : data.getExpressListList()) {
                    Expression express = new Expression();
                    express.setId(response.getExpressId());
                    express.setName(response.getExpressName());
                    expressionList.add(express);
                }

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getExpressionListCallback.onGetExpressionList(expressionList);
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getExpressionListCallback.onError(e.getErrorCode());
                    }
                });

            }
        });
    }

    public void playExpression(String expressionName, final IExpression.ControlCallback playExpressionCallback) {

        CodeMaoPlayExpression.PlayExpressionRequest.Builder builder = CodeMaoPlayExpression.PlayExpressionRequest.newBuilder();
        builder.setExpressName(expressionName);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_PLAY_EXPRESS_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoPlayExpression.PlayExpressionResponse>() {
            @Override
            public void onSuccess(final CodeMaoPlayExpression.PlayExpressionResponse data) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            playExpressionCallback.onSuccess();
                        }else {
                            playExpressionCallback.onError(data.getResultCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        playExpressionCallback.onError(e.getErrorCode());
                    }
                });

            }
        });
    }

    public void startBehavior(String behaviorName, final IExpression.ControlCallback controlCallback) {

        CodeMaoControlBehavior.ControlBehaviorRequest.Builder builder = CodeMaoControlBehavior.ControlBehaviorRequest.newBuilder();
        builder.setName(behaviorName);
        builder.setEventType(Statics.BEHAVIOR_PLAY);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_BEHAVIOR_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoControlBehavior.ControlBehaviorResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlBehavior.ControlBehaviorResponse data) {

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

    public void stopBehavior( final IExpression.ControlCallback controlCallback) {

        CodeMaoControlBehavior.ControlBehaviorRequest.Builder builder = CodeMaoControlBehavior.ControlBehaviorRequest.newBuilder();
        builder.setEventType(Statics.BEHAVIOR_STOP);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_BEHAVIOR_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoControlBehavior.ControlBehaviorResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlBehavior.ControlBehaviorResponse data) {

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
