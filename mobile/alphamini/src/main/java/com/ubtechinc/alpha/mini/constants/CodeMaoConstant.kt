package com.ubtechinc.alpha.mini.constants;

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/3/30 14:26
 */

object CodeMaoConstant {

    /**********************************************JavaInterface */
    //接口名称
    const val JS_INTERFACE_NAME = "blocklyObj"

    //拍照结果返回
    const val JS_TAKE_PIC_RESULT_STATE = "javascript:takePicResult(%s)"
    //返回键
    const val JS_REPORT = "javascript:reportEvent(%s)"
    const val JS_CALLBACK = "javascript:callback(%s)"
    const val JS_START_RENDER = "javascript:start_render(\"%s\")"
    /************************************常量库**************/
    //TTS播报
    const val EVENT_TYPE_PLAY_TTS = 1
    //播放动作
    const val EVENT_TYPE_PLAY_ACTION = 2
    //播放表情
    const val EVENT_TYPE_PLAY_EXPRESSION = 3
    //停止动作
    const val EVENT_TYPE_STOP_ACTION = 4
    //拍照
    const val EVENT_TYPE_TAKE_PIC = 5
    //移动机器人
    const val EVENT_TYPE_MOVE_ROBOT = 6
    //翻译模式切换
    const val EVENT_TYPE_SWITCH_TRANSLATE = 7
    //开关嘴巴灯
    const val EVENT_TYPE_SWITCH_MOUTH_LAMP = 8
    //设置嘴巴灯颜色
    const val EVENT_TYPE_SET_MOUTH_LAMP = 9
    //控制表现力
    const val EVENT_TYPE_CONTROL_BEHAVIOR = 10
    //人脸注册
    const val EVENT_TYPE_CONTROL_REGISTER_FACE = 11
    //添加作品
    const val EVENT_TYPE_ADD_PRODUCT = 12
    //删除作品
    const val EVENT_TYPE_DELETE_PRODUCT = 13
    //恢复到原始状态
    const val EVENT_TYPE_REVERT_ROBOT_ORIGINAL = 14
    //TTS停止
    const val EVENT_TYPE_STOP_TTS = 15
    //机器人状态
    const val EVENT_TYPE_ROBOT_STATUS = 16
    //获取动作列表
    const val EVENT_TYPE_GET_ACTION_LIST = 17
    //识别人脸数量
    const val EVENT_TYPE_FACE_DETECT = 18
    //人脸分析
    const val EVENT_TYPE_FACE_ANALYSIS = 19
    //物体识别
    const val EVENT_TYPE_OBJECT_DETECT = 20
    //获取已注册的人脸信息
    const val EVENT_TYPE_GET_REGISTER_FACES = 21
    //人脸识别
    const val EVENT_TYPE_FACE_RECOGNISE = 22
    //获取表情列表
    const val EVENT_TYPE_GET_EXPRESSION_LIST = 23
    //保存编程作品详情
    const val EVENT_TYPE_GET_PRODUCT_DETAIL = 24
    //获取编程作品列表
    const val EVENT_TYPE_GET_PRODUCT_LIST = 25
    //获取是否达到运行条件
    const val EVENT_TYPE_CHECK_RUN_CONDITION = 26
    /************************主动上报****************************************/
    //上报电池信息
    const val EVENT_TYPE_REPORT_ROBOT_BATTERY = 101
    //上报红外距离
    const val EVENT_TYPE_REPORT_INFRARED_DISTANCE = 102
    //上报机器人姿态
    const val EVENT_TYPE_REPORT_ROBOT_POSTURE = 103
    //上报机器人拍头
    const val EVENT_TYPE_REPORT_ROBOT_HEAD_RACKET = 104
    //上报音量键触碰
    const val EVENT_TYPE_REPORT_ROBOT_VOLUME_KEY_PRESS = 105
    //上报手机方向改变
    const val EVENT_TYPE_REPORT_PHONE_DIRECTION = 106
    //上报寻找人脸状态
    const val EVENT_TYPE_FIND_FACE_STATE = 107
    //上报机器人状态
    const val EVENT_TYPE_REPORT_ROBOT_STATUS = 108
    //上报手机返回键
    const val EVENT_TYPE_REPORT_PHONE_KEY_DOWN_BACK = 109
    //上报视频已播放完
    const val EVENT_TYPE_REPORT_VIDEO_PLAY_FINISH = 110
    /*****************数据Key************************/
    const val KEY_IS_CONNECT = "isConnected"
    const val KEY_FACE_COUNT = "faceCount"
    const val KEY_OBJECTS = "objects"
    const val KEY_LEVEL = "level"
    const val KEY_STATUS = "status"
    const val KEY_DISTANCE = "distance"
    const val KEY_TYPE = "type"
    const val KEY_PHONE_DIRECTION = "direction"
    const val KEY_PRODUCT_CONTENT = "content"
    const val KEY_IS_SUCCESS = "isSuccess"
    const val KEY_PRODUCT_ID = "productId"
    const val KEY_IS_CAN_RUN = "isCanRun"
    const val PARAM_HOME = "home"
}
