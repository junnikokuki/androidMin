package com.ubtechinc.codemaosdk.functions;

import com.ubtechinc.codemao.CodeMaoActionInfo;
import com.ubtechinc.codemao.CodeMaoGetActionList;
import com.ubtechinc.codemao.CodeMaoMoveRobot;
import com.ubtechinc.codemao.CodeMaoPlayAction;
import com.ubtechinc.codemao.CodeMaoStopAction;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.bean.RobotAction;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.interfaces.IAction;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

import java.util.ArrayList;
import java.util.List;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/2 15:53
 */

public class ActionApi {
    private ActionApi() {}
    private static ActionApi actionApi = new ActionApi();
    public static ActionApi get() {
        if(actionApi == null) {
            synchronized (ActionApi.class) {
                actionApi = new ActionApi();
            }

        }
        return actionApi;
    }

    public void getActionList(final IAction.getActionListCallback getActionListCallback) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_ACTION_LIST_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoGetActionList.GetActionListResponse>() {
            @Override
            public void onSuccess(final CodeMaoGetActionList.GetActionListResponse data) {
                final List<RobotAction> actionList = new ArrayList<>();
                for (CodeMaoActionInfo.ActionInfoResponse response : data.getActionListList()) {
                    RobotAction actionInfo = new RobotAction();
                    actionInfo.setId(response.getId());
                    actionInfo.setCnName(response.getCnName());
                    actionInfo.setEnName(response.getEnName());
                    actionInfo.setType(response.getType());
                    actionList.add(actionInfo);
                }

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                            getActionListCallback.onGetActionList(actionList);
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getActionListCallback.onError(e.getErrorCode());
                    }
                });

            }
        });
    }

    public void playAction(String actionId, final IAction.playActionCallback playActionCallback) {

        CodeMaoPlayAction.PlayActionRequest.Builder builder = CodeMaoPlayAction.PlayActionRequest.newBuilder();
        builder.setActionName(actionId);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_PLAY_ACTION_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<  CodeMaoPlayAction.PlayActionResponse>() {
            @Override
            public void onSuccess(final  CodeMaoPlayAction.PlayActionResponse data) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            playActionCallback.onCompleted();
                        }else {
                            playActionCallback.onError(data.getResultCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        playActionCallback.onError(e.getErrorCode());
                    }
                });

            }
        });
    }

    public void stopAction(final IAction.stopActionCallback stopActionCallback) {

        Phone2RobotMsgMgr.get().sendData(CmdId.BL_STOP_ACTION_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback< CodeMaoStopAction.StopActionResponse>() {
            @Override
            public void onSuccess(final CodeMaoStopAction.StopActionResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            stopActionCallback.onSuccess();
                        }else {
                            stopActionCallback.onError(data.getResultCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        stopActionCallback.onError(e.getErrorCode());
                    }
                });
            }
        });
    }

    public void moveRobotByCustom(byte direction, int step, final IAction.playActionCallback playActionCallback) {

        CodeMaoMoveRobot.MoveRobotRequest.Builder builder = CodeMaoMoveRobot.MoveRobotRequest.newBuilder();
        builder.setDirection(direction);
        builder.setStep(step);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_MOVE_ROBOT_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback< CodeMaoMoveRobot.MoveRobotResponse>() {
            @Override
            public void onSuccess(final CodeMaoMoveRobot.MoveRobotResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            playActionCallback.onCompleted();
                        }else {
                            playActionCallback.onError(data.getCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        playActionCallback.onError(e.getErrorCode());
                    }
                });
            }
        });

    }

}
