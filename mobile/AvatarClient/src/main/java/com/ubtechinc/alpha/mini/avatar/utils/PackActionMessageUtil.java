package com.ubtechinc.alpha.mini.avatar.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ubt on 2017/6/8.
 * 拼接向机器人发送命令的工具类
 */

public class PackActionMessageUtil {
    private static final String BODY_TYPE = "3";
    private static final String HEAD_TYPE = "2";
    private static final String WALK_TYPE = "5";
    private static final String LEVEL_CHANNEL_TYPE = "4";
    private static final String TAKE_PHOTO_TYPE = "6";
    private static final String STOP_WALK_TYPE = "7";
    private static final String HEARTBEATS_TYPE = "8";
    private static PackActionMessageUtil instance;

    private PackActionMessageUtil() {

    }

    public static PackActionMessageUtil getInstance() {
        if (instance == null) {
            synchronized (PackActionMessageUtil.class) {
                if (instance == null) {
                    instance = new PackActionMessageUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 执行动作
     *
     * @param actionFile
     */
    public void executionBodyAction(String actionFile) {
        JSONObject jsonObject = packActionControl(actionFile);
        sendControlCommand(jsonObject);
    }

    /**
     * 执行头部转动动作
     *
     * @param orientation
     */
    public void excutionHeadAction(int orientation) {
        JSONObject jsonObject = packHeadControl(orientation);
        sendControlCommand(jsonObject);
    }

    /**
     * 执行行走动作
     */
    public void excutionWalkAction(int orientation) {
        JSONObject jsonObject = packWalkingControl(orientation);
        sendControlCommand(jsonObject);
    }

    /**
     * 执行离开房间命令
     */
    public void excutionLevelChannelAction() {
        JSONObject jsonObject = packLeaveChannelControl();
        sendControlCommand(jsonObject);
    }

    /**
     * 执行拍照命令
     */
    public void excutionTakePhotoAction() {
        JSONObject jsonObject = packTakePhotoControl();
        sendControlCommand(jsonObject);
    }

    /**
     * 执行停止行走命令
     */
    public void excutionStopWalkingAction() {
        JSONObject jsonObject = packStopWalkingControl();
        sendControlCommand(jsonObject);
    }

    /**
     * 通过json打包动作命令
     *
     * @param actionFile
     * @return
     */
    public JSONObject packActionControl(String actionFile) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", BODY_TYPE);
            jsonObject.put("Control", "body");
            jsonObject.put("ActionFile", actionFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通过json打包头部旋转命令
     */
    public JSONObject packHeadControl(int orientation) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", HEAD_TYPE);
            jsonObject.put("Control", "head");
            jsonObject.put("Orientation", orientation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 通过json打包行走命令
     *
     * @return
     */
    public JSONObject packWalkingControl(int orientation) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", WALK_TYPE);
            jsonObject.put("Control", "Walk");
            jsonObject.put("Orientation", orientation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通过json打包停止行走命令
     *
     * @return
     */
    public JSONObject packStopWalkingControl() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", STOP_WALK_TYPE);
            jsonObject.put("Control", "StopWalking");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通过json打包拍照命令
     *
     * @return
     */
    public JSONObject packTakePhotoControl() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", TAKE_PHOTO_TYPE);
            jsonObject.put("Control", "TakePhoto");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 发送离开消息
     */
    public JSONObject packLeaveChannelControl() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type", LEVEL_CHANNEL_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject packHeartbeats() {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, String> map = new HashMap<>(2);
            map.put("commandId", "1904");
            map.put("version", "1");
            jsonObject.put("header", map);
            jsonObject.put("Type", HEARTBEATS_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void sendControlCommand(JSONObject jsonObj) {
        try {
// TODO           UBTIMApi.getIMApiInstance(mContext).sendCommandThirdMsg(new String(Base64.encode(jsonObj.toString().getBytes(), Base64.DEFAULT), "UTF-8")
//                    , Constants.MAIN_SERVICE_SURVEILLANCE_APP_KEY, RobotManagerService.getInstance().robotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
