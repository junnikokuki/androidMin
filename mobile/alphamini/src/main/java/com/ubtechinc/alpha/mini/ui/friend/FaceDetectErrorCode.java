package com.ubtechinc.alpha.mini.ui.friend;

/**
 * @Date: 2017/12/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 人脸检测返回的对应错误码
 */

class FaceDetectErrorCode {

    public static final int SUCCESS_DETECT =  0;// 0:绑定成功
    public static final int NO_FACE_DETECT =  1;// 1:失败，未检测到人脸
    public static final int SAME_FACE_DETECT =  2;// 2:无法录入相同的人脸
    public static final int TOO_MUCH_FACE_DETECT =  3;// 3:人脸信息数量达到上限
    public static final int FACE_FAILER_DETECT =  4;//4:人脸采集失败
    public static final int FACE_DETECTING =  5;//5:当前正在录入人脸
    public static final int TOO_MUCH_FACE_IN_ROBOT_DETECT =  6;//6:人脸数量太多
    public static final int INTERUPTER_DETECTING =  7;//7:被打断
    public static final int LOW_POWER =  8;//8:低电量
    public static final int NETWORK_ERROR =  9;//9网络错误

    public final static int ERROR_ROBOT_BESY = 10; //机器人忙碌（ 提示：mini正忙碌，请稍后再试）
    public final static int ERROR_ROBOT_BESY_BOOK = 11; //机器人忙碌（ 提示：mini正在绘本阅读，请稍后再试）
    public final static int ERROR_ROBOT_BESY_AVATAR = 12; //机器人忙碌（ 提示：Mini正处于监控中，请稍后再试）
    public final static int ERROR_ROBOT_BESY_CALL = 13; //机器人忙碌（ 提示：Mini正在通话中，请稍后再试）

}
