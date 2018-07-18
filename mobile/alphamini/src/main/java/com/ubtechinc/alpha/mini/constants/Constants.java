package com.ubtechinc.alpha.mini.constants;

import android.util.SparseArray;

public class Constants {

    public static final SparseArray analysisBtn = new SparseArray();

    public static final String ROBOT_BBMINI = "Mini";//机器人标志

    public static final String ROBOT_TAG = "Mini_";//机器人显示的TAG

    public static final String DATA_COMMAND = "co";//蓝牙传输指令



    public static final String WIIF_LIST_COMMAND = "p";// 机器人响应的WiFi列表

    public static final int WIFI_LIST_TRANS = 1;// WiFi列表的请求和响应

    public static final int WIFI_LIST_RESLUT_TRANS = 101; //返回wifi列表的指令

    public static final int WIFI_INFO_TRANS = 2;// WIFI 信息发送

    public static final int ROBOT_CONNENT_WIFI_SUCCESS = 102;//机器人连接WiFi成功指令

    public static final int ROBOT_BLE_NETWORK_FAIL = -1;//机器人蓝牙配网失败

    public static final int CLIENT_ID_TRANS = 3;// 发送client id 命令

    public static final int ROBOT_BANGDING_SUCCESS = 103;//机器人绑定成功指令

    public static final int CONNECT_SUCCESS = 4;//蓝牙连接成功指令

    public static final int ROBOT_CONNECT_SUCCESS = 104; //机器人回复手机端连接成功指令

    public static final int ROBOT_WIFI_IS_OK_TRANS = 5;//发送机器人是否在线

    public static final int ROBOT_REPLY_WIFI_IS_OK_TRANS = 105;//机器人回复WiFi是否连接指令

    public static final String REQUEST_SHARE_TIME = "request_share_time";
    public static String CODE = "cd";
    public static final String ERROR_CODE = "ec";

    public static final String PRODUCTID = "pid";

    public static final String SERISAL_NUMBER = "sid";

    public static final String CLIENTID = "cid";

    public static final String DEBUG_MODE = "DEBUG_MODE";

    /**
     * WiFi连接失败错误码
     */
    public static int CONNECT_WIFI_FAILED_ERROR_CODE = 0;

    /**
     * ping 网址失败的错误码
     */
    public static int PING_ERROR_CODE = 2;

    /**
     * 登录TVS错误码
     */
    public static int TVS_LOGIN_ERROR_CODE = 3;

    /**
     * WiFi密码不合法错误码
     */
    public static int PASSWORD_VALIDATA_ERROR_CODE = 1;

    /**
     * 连接WiFi超时错误码
     */
    public static int CONNECT_TIME_OUT_ERROR_CODE = 4;

    /**
     * 已经有设备连接机器人错误码
     */
    public static int ALEARDY_CONNECT_ERROR_CODE = 5;

    /**
     * 注册tvs失败错误码
     */
    public static int TVS_ERROR_CODE = 6;

    /**
     * 绑定机器人失败错误码
     */
    public static int REGISTER_ROBOT_ERROR_CODE = -1;

    /**
     * 获取clientId失败错误码
     */
    public static int GET_CLIENT_ID_ERROR_CODE = -2;

    /**
     * 发送蓝牙信息失败
     */
    public static int BLUETOOTH_SEND_FIAL = -3;

    /**
     * 没有序列号错误码
     */
    public static int ON_SERAIL_ERROR_CODE = 7;

    /**
     * 蓝牙断开连接
     */
    public static int BLE_LOST_CONNECT = 9;

    /**
     * 已经被绑定
     */
    public static int ALREADY_BADING = 8;

    public static int CODE_0 = 0;

    public static int CODE_1 = 1;

    public static String RESPONSE_CODE_SUCCESS = "1";

    /**
     * 微信APPID
     */
    public static String WX_APP_ID = "wx609d79e0bd3d4d9b";

    /**
     * QQ openID
     */
    public static String QQ_OPEN_ID = "1106279915";

    /**
     * 模拟ID
     */
    public static String ROBOT_USER_ID = "020209UBT27021700001";
//    public static String ROBOT_USER_ID = "020212UBT27032900011";

    /**
     * 共享权限界面
     */
    public static int PERMISSION_REQUEST_KEY = 1024;

    /**
     * 分享类型
     */
    public static final String KEY_SHARE_TYPE = "share_type";

    /**
     * 分享类型QQ
     */
    public static final int SHARE_TYPE_QQ = 1;

    /**
     * 分享类型微信
     */
    public static final int SHARE_TYPE_WECHAT = 2;

    /**
     * Intent返回权限Key
     */
    public static String PERMISSION_KEY = "key_permission";

    public static String SHARE_TIME = "key_share_time";
    public static String SLAVE_USER_ID = "slave_user_id";
    public static final String USERID_KEY = "key_user_id";
    public static final String USERNAME_KEY = "key_user_name";
    public static final String USER_MESSAGE = "key_user_message";
    public static final String UNREAD_COUNT = "key_unread_count";
    public static final String RELATIONDATE_KEY = "key_relation_date";
    public static final String PERMISSIONCODE_AVATAR = "Avatar";
    public static final String PERMISSIONCODE_PHOTO = "PhotoAlbum";


    /***im切换WiFi错误吗***/
    public static final String SUCCESS_CODE = "0";//切换WiFi成功
    public static final String PWD_ERROR_CODE = "1";//密码错误
    public static final String TIME_OUT_CODE = "2";//超时

    public static final String ARLEADY_CONNECT_WIFI = "3";//已经连接该网络


    /*服务端放回的错误码*/
    public static final int APPLY_HANDEL_ERROR = 7003; //无权审批改消息

    //public static final String PERMISSION_CODE_PHOTOALBUM = "PhotoAlbum";
    //public static final String PERMISSION_CODE_AVATAR = "PhotoAlbum";
    public static final String PERMISSIONCODE_CALL = "Call";
    public static final String PERMISSION_CODE_CODING = "Coding";

    public static final String USER_HOST_INNER = "";
    public static final String XINGE_HOST_INNER = "";

    public static final String USER_HOST_OUTER = "";
    public static final String XINGE_HOST_OUTER = "";

    public static final int CONTACT_ERROR_INVALID_PARAMS = 1; //通讯录操作出错，contactId 不存在
    public static final int CONTACT_ERROR_OPTION_CONFILCT = 2;//通讯录操作出错，操作冲突
    public static final int CONTACT_ERROR_UNKOWN_ERROR = 3;//通讯录操作出错，未知错误


    public static final String CUSTOMERS_CALL = "4006666700";


    /***************************************jimu car相关*********************************************************/
    public static final int CAR_LOW_POWER = 1;
    public static final int ROBOT_LOW_POWER = 2;
    public static final int ALL_LOW_POWER = 3;

    public static final String JIMU_CAR_BELL = "jimu_car_bell";
    public static final String JIMU_CAR_POLICE_BELL = "jimu_car_police_siren";
    /***************************************jimu car相关**********************************************************/
}
