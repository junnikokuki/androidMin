package com.ubtechinc.codemaosdk;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/4/2 14:55
 */

public class Statics {
    public static final int SDK_VERSION = 1;
    public static final byte PROTO_VERSION = 0x01;
    public static final String UUID_SERVICE = "10057e66-28bd-4c3e-a030-08b4982739bc";
    public static final String UUID_WRITE_CHARACTER = "d2f9ff26-ad08-4bd3-8436-9b8f33bdea76";
    public static final String UUID_READ_CHARACTER = "7e0dffa2-c44f-48ac-a39e-3fce46d9aabb";
    public static final String UUID_HEART_CHARACTER = "ebe066b9-a0c7-4ff4-9c05-6a58a9344364";
    public static final byte MOUTH_LAMP_MODLE_NORMAL = 0x00;
    public static final byte MOUTH_LAMP_MODLE_BREATH = 0x01;
    public static final short MOUTH_LAMP_COLOR_RED = 1;
    public static final short MOUTH_LAMP_COLOR_GREEN = 2;
    public static final short MOUTH_LAMP_COLOR_BLUE = 3;

    public static final byte HEAD_RACKET_CLICK = 0x01;
    public static final byte HEAD_RACKET_LONGPRESS = 0x02;
    public static final byte HEAD_RACKET_DOUBLECLICK = 0x03;
    public static final byte HEAD_RACKET_UNRECOGNIZED = 0x00;

    public static final byte VOLUME_KEY_UP = 0x01;
    public static final byte VOLUME_KEY_DOWN = 0x00;

    public static final byte TAKE_PIC_IMMEDIATELY = 0x00;
    public static final byte TAKE_PIC_WITH_FACE_DETECT = 0x01;
    /******************寻找人脸状态*******************/
    public static final byte FIND_FACE_STATUS_FAIL = 0x00;
    public static final byte FIND_FACE_STATUS_START = 0x01;
    public static final byte FIND_FACE_STATUS_PAUSE = 0x02;
    public static final byte FIND_FACE_STATUS_STOP = 0x03;
    public static final byte FIND_FACE_STATUS_CHANGE = 0x04;
    /******************人脸寻找操作*******************/
    public static final int FIND_FACE_CONTROL_START = 1;
    public static final int FIND_FACE_CONTROL_PAUSE = 2;
    public static final int FIND_FACE_CONTROL_STOP = 3;
    /*****************机器人姿态*********************/
    public static final byte ROBOT_POSTURE_DEFAULT = 0;
    public static final byte ROBOT_POSTURE_STAND = 1;
    public static final byte ROBOT_POSTURE_SPLITS_LEFT = 2;
    public static final byte ROBOT_POSTURE_SPLITS_RIGHT = 3;
    public static final byte ROBOT_POSTURE_SITDOWN = 4;
    public static final byte ROBOT_POSTURE_SQUATDOWN = 5;
    public static final byte ROBOT_POSTURE_KNEELING = 6;
    public static final byte ROBOT_POSTURE_LYING = 7;
    public static final byte ROBOT_POSTURE_LYINGDOWN = 8;
    public static final byte ROBOT_POSTURE_SPLITS_LEFT_1 = 9;
    public static final byte ROBOT_POSTURE_SPLITS_RIGHT_2 = 10;
    public static final byte ROBOT_POSTURE_UNRECOGNIZED = -1;
    /***************机器人跌倒爬起状态*******************/
    public static final byte ROBOT_FALL_UP_START_FALLCLIMB = 0;
    public static final byte ROBOT_FALL_UP_FINISH_FALLCLIMB = 1;

    public static final byte DIRECTION_LEFT = 0x01;
    public static final byte DIRECTION_RIGHT = 0x02;
    public static final byte DIRECTION_FORWARD = 0x03;
    public static final byte DIRECTION_BACKWARD = 0x04;

    public static final byte BEHAVIOR_PLAY = 0x01;
    public static final byte BEHAVIOR_STOP = 0x00;

    public static final byte FACE_REGISTER_START = 0x01;
    public static final byte FACE_REGISTER_STOP = 0x00;

    public static final byte TTS_PLAY = 0x01;
    public static final byte TTS_STOP = 0x00;

    public static final byte BLE_CONNECTED = 0x01;
    public static final byte BLE_DISCONNECT = 0x00;
}
