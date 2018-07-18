package com.ubtechinc.alpha.mini.avatar.common;

/**
 * [业务相关常量]
 *
 * @author：唐宏宇
 * @date：2016/6/27 14:51
 * @modifier：
 * @modify_date：
 * [A brief description]
 * version
 */
public class BusinessConstants {

    //未登陆过
    public static final int LOGIN_STATE_NO_LOGIN = 0;
    //登录过期
    public static final int LOGIN_STATE_LOGIN_TIME_OUT = 2;
    //登录有效
    public static final int LOGIN_STATE_LOGIN_EFFECTIVE = 1;

    /*************************通知消息**************************************/
    public static final int NOTICE_TYPE_SYS_MSG = 2; // 系统消息
    public static final int NOTICE_TYPE_CHAT_MSG = 3;// 聊天消息，已停用
    public static final int NOTICE_TYPE_AUTHORIZE_MSG = 1; // 授权消息
    public static final int NOTICE_TYPE_CHAT_TYPE_PIC = 5; // 图片消息
    public static final int NOTICE_STATE_READ = 1;//已读
    public static final int NOTICE_STATE_UNREAD = 0;//未读
   // 1 表示授權  2 表示主动刪除授權 3表示给主人提示我同意授权 4 表示被删除授权
    public static final int AUTHORIZATION_TYPE_AUTHORIZE = 1;//授权
    public static final String XMPP_MESSAGE_TYPE = "addrobotfriend";//机器人授权
    public static final String ROBOT_CONTROL_START_OK = "start ok";//机器人授权
    public static final String ROBOT_CONTROL_START_FAIL = "start fail";//机器人授权
    public static final String ROBOT_NEED_TOKEN ="lynx_need_token";
    public static final int ROBOT_STATE_OFFLINE = 0;
    public static final int ROBOT_STATE_ONLINE = 1;
    // 自己用
    public static final int ROBOT_STATE_USED_BY_SELF = 2;
    // 别人用
    public static final int ROBOT_STATE_USED_BY_OTHER = 3;
    public static final int NET_ERROR_INTT_DATA_FAILED =4;//网络异常数据没有加载出来
    public static final int LOADING_DATA = 5;
}
