package com.ubtechinc.alpha.mini.common;

import android.webkit.JavascriptInterface;

import com.ubtech.utilcode.utils.LogUtils;

/**
 * @className BlocklyJsInterface
 *
 * @author wmma
 * @description android端和js交互接口
 * @date  2017/03/06
 * @update
 */


public class BlocklyJsInterface {

    private String TAG = "BlocklyJsInterface";

    private IBlocklyControl iBlocklyControl;

    public BlocklyJsInterface(IBlocklyControl iBlocklyControl) {
        this.iBlocklyControl = iBlocklyControl;
    }


    @JavascriptInterface
    public void bluetoothManage() {
        //TODO 连接机器人
        LogUtils.i(TAG, "bluetoothManage");
        iBlocklyControl.bluetoothManage();
    }


    @JavascriptInterface
    public void getRobotConnectionState() {
        //TODO 获取机器人连接状态
        LogUtils.i(TAG, "getRobotConnectionState");
        iBlocklyControl.getRobotConnectionState();
    }

    @JavascriptInterface
    public void playTTS(String text) {
        //TODO 播放TTS
        LogUtils.i(TAG, "playTTS");
        iBlocklyControl.playTTS(text);
    }

    @JavascriptInterface
    public void stopTTS() {
        //TODO 播放TTS
        LogUtils.i(TAG, "stopTTS");
        iBlocklyControl.stopTTS();
    }
    @JavascriptInterface
    public void getActionList() {
        //TODO 获取动作列表
        LogUtils.i(TAG, "getActionList");
        iBlocklyControl.getActionList();
    }
    @JavascriptInterface
    public void playAction(String actionName) {
        //TODO 播放动作
        LogUtils.i(TAG, "playAction");
        iBlocklyControl.playAction(actionName);
    }

    @JavascriptInterface
    public void stopAllAction() {
        //TODO 停止所有动作
        LogUtils.i(TAG, "stopAllAction");
        iBlocklyControl.stopAllAction();
    }

    @JavascriptInterface
    public void faceDetect(int timeout) {
        //TODO 识别人脸数
        LogUtils.i(TAG, "faceDetect");
        iBlocklyControl.faceDetect(timeout);
    }

    @JavascriptInterface
    public void faceAnalysis(int timeout) {
        //TODO 识别性别
        LogUtils.i(TAG, "faceAnalysis");
        iBlocklyControl.faceAnalysis(timeout);
    }

    @JavascriptInterface
    public void faceRecognise(int timeout) {
        //TODO 识别性别
        LogUtils.i(TAG, "faceRecognise");
        iBlocklyControl.faceRecognise(timeout);
    }

    @JavascriptInterface
    public void recogniseObject(String content) {
        //TODO 识别水果
        LogUtils.i(TAG, "objectDetect");
        iBlocklyControl.objectDetect(content);
    }

    @JavascriptInterface
    public void getExpressionList() {
        //TODO 获取表情列表
        LogUtils.i(TAG, "getExpressionList");
        iBlocklyControl.getExpressionList();
    }
    @JavascriptInterface
    public void playExpression(String expression) {
        //TODO 播放表情
        LogUtils.i(TAG, "playExpression ");
        iBlocklyControl.playExpression( expression);
    }


    @JavascriptInterface
    public void controlMouthLamp(boolean isOpen) {
        //TODO 获取表情列表
        LogUtils.i(TAG, "controlMouthLamp");
        iBlocklyControl.controlMouthLamp(isOpen);
    }
    @JavascriptInterface
    public void setMouthLamp(String content) {
        //TODO 获取表情列表
        LogUtils.i(TAG, "setMouthLamp");
        iBlocklyControl.setMouthLamp(content);
    }

    @JavascriptInterface
    public void takePicture(int type) {
        //TODO 获取表情列表
        LogUtils.i(TAG, "takePicture");
        iBlocklyControl.takePicture(type);
    }

    @JavascriptInterface
    public void controlFindFace(String content) {
        //TODO 获取表情列表
        LogUtils.i(TAG, "controlFindFace");
        iBlocklyControl.controlFindFace(content);
    }
    @JavascriptInterface
    public void moveRobot(String content) {
        //TODO 移动机器人
        LogUtils.i(TAG, "moveRobot");
        iBlocklyControl.moveRobot(content);
    }
    @JavascriptInterface
    public void switchTranslateModel(boolean isEnter) {
        //TODO 切换翻译模式
        LogUtils.i(TAG, "switchTranslateModel");
        iBlocklyControl.switchTranslateModel(isEnter);
    }

    @JavascriptInterface
    public void controlRegisterFace(String content) {
        //TODO 控制注册人脸
        LogUtils.i(TAG, "controlRegisterFace");
        iBlocklyControl.controlRegisterFace(content);
    }

    @JavascriptInterface
    public void controlBehavior(String content) {
        //TODO 控制表现力
        LogUtils.i(TAG, "controlBehavior");
        iBlocklyControl.controlBehavior(content);
    }

    @JavascriptInterface
    public void loadProductList() {
        //TODO 获取作品列表
        LogUtils.i(TAG, "loadProductList");
        iBlocklyControl.loadProductList();
    }

    @JavascriptInterface
    public void saveProduct(String content) {
        //TODO 保存作品
        LogUtils.i(TAG, "saveProduct");
        iBlocklyControl.saveProduct(content);
    }

    @JavascriptInterface
    public void deleteProduct(String id) {
        //TODO 删除作品
        LogUtils.i(TAG, "deleteProduct");
        iBlocklyControl.deleteProduct(id);
    }

    @JavascriptInterface
    public void getProductContent(String id) {
        //TODO 获取作品详情
        LogUtils.i(TAG, "getProductContent");
        iBlocklyControl.getProductContent(id);
    }

    @JavascriptInterface
    public void getRegisterFaces() {
        //TODO 获取已注册的人脸信息
        LogUtils.i(TAG, "getRegisterFaces");
        iBlocklyControl.getRegisterFaces();
    }

    @JavascriptInterface
    public void revertRobotOrigin() {
        //TODO 恢复机器人原始状态
        LogUtils.i(TAG, "revertRobotOrigin");
        iBlocklyControl.stopRunProgram();
    }

    @JavascriptInterface
    public void exitCodingModel() {
        //TODO 退出编程模式
        LogUtils.i(TAG, "exitCodingModel");
        iBlocklyControl.exitCodingModel();
    }
    @JavascriptInterface
    public void checkRunCondition() {
        //TODO 检查运行条件
        LogUtils.i(TAG, "checkRunCondition");
         iBlocklyControl.checkRunCondition();
    }

    @JavascriptInterface
    public void startPlayVideo(String url) {
        //TODO 播放视频
        LogUtils.d( "startPlayVideo url = "+ url);
        iBlocklyControl.startPlayVideo(url);
    }

    @JavascriptInterface
    public void upload(String data) {
        iBlocklyControl.upload(data);
    }
    @JavascriptInterface
    public void startRunProgram() {
        //TODO 播放视频
        LogUtils.i( "startRunProgram ");
        iBlocklyControl.checkRunCondition();

    }
    @JavascriptInterface
    public void stopRunProgram() {
        //TODO 播放视频
        LogUtils.i( "stopRunProgram ");
        iBlocklyControl.stopRunProgram();

    }
}
