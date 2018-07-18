package com.ubtechinc.alpha.mini.avatar.common;

/**
 * @author will on 5/30/2016.
 */

public class Constants {
    //public static final String PRODUCT_ID = "interactive_conversation";
    public static final String PRODUCT_ID = "my_device";

    /**
     * 主服务视频监控应用APP KEY
     * */
    public static final String MAIN_SERVICE_SURVEILLANCE_APP_KEY = "BE2BC2AA3A0F6B084C1E6BAB66ED26F6";

    /**
     * 主服务监控应用名称
     * */
    public static final String MAIN_SERVICE_SURVEILLANCE_NAME = "视频监控";

    /**
     * 主服务监控应用包名
     * */
    public static final String MAIN_SERVICE_SURVEILLANCE_PACKAGE_NAME = "com.alpha2.videoSupervision";

    /**
     * 声网App ID
     * */
    public static final String APP_ID = "bd63e74c3e2d4d428fd2fc7a0d2f741c";

    /**
     * 没有查询到电量的时候，查询间隔
     * */
    public static final int POWER_REQUEST_INTERVAL_WITHOUT_OBTAINED = 2000;//2s

    /**
     * 查询电量的间隔时间
     * */
    public static final int POWER_REQUEST_INTERVAL = 60*1000;//3分钟

    /**
     * 技能界面跳转extra参数
     * */
    public static final String EXTRA_SKILL_NAME = "skill_name";
    public static final String EXTRA_SKILL_DANCE = "skill_dance";
    public static final String EXTRA_SKILL_PHOTO = "skill_photo";
    public static final String EXTRA_SKILL_WORKOUT = "skill_workout";
    public static final String EXTRA_SKILL_SURVEILLANCE = "skill_surveillance";

    /**跳舞技能类型*/
    public static final int SKILL_TYPE_DANCE_ID = 1;
    /**拍照技能类型*/
    public static final int SKILL_TYPE_PHOTO_ID = 2;
    /**运动技能类型*/
    public static final int SKILL_TYPE_WORKOUT_ID = 3;

    /**跳舞技能类型*/
    public static final String SKILL_TYPE_DANCE_TITLE = "Lynx Dance";
    /**拍照技能类型*/
    public static final String SKILL_TYPE_PHOTO_TITLE = "Lynx Cam+";
    /**运动技能类型*/
    public static final String SKILL_TYPE_WORKOUT_TITLE = "Workout Exercises";

    /**瑜伽技能类型*/
    public static final String SKILL_TYPE_YOGA_TITLE = "Yoga";


    /**技能详情界面查询类型:跳舞*/
    public static final String SKILL_TYPE_DANCE = "dance";
    /**技能详情界面查询类型:拍照*/
    public static final String SKILL_TYPE_PHOTO = "photo";
    /**技能详情界面查询类型:运动*/
    public static final String SKILL_TYPE_WORKOUT = "workout";
    /**技能详情界面查询类型:监控*/
    public static final String SKILL_TYPE_MONITOR = "monitor";

    /**技能推送的广播*/
    public static final String SKILL_HISTORY_ACTION = "alexa_skill_action";
    /**照片推送的广播**/
    public static final String PHOTO_HISTORY_ACTION = "alexa_photo_action";

    /**技能推送的广播title*/
    public static final String EXTRA_SKILL_HISTORY_TITLE = "skill_title";
    /**技能推送的广播message*/
    public static final String EXTRA_SKILL_HISTORY_MSG = "skill_msg";

    /**SharePreference中保存机器人mic key值*/
    public static final String PREF_KEY_MIC_STATUS = "mic_status";

    /**新消息过来广播*/
    public static final String NEWS_ARRIVE_ACTION = "news_arrive_action";

    /**打开边充边玩广播*/
    public static final String ACTION_OPEN_CHARGE_AND_PLAY = "open_charge_and_play";

    /**preference相册同步开关*/
    public static final String PREF_KEY_PHOTO_SYNC_SWITCH = "photo_sync_switch";

    /**preference相册同步开关*/
    public static final String PREF_KEY_PHOTO_SYNC_SWITCH_FIRST = "photo_sync_switch_first";

    /**preference图像验证key*/
    public static final String PREF_KEY_AUTH_IMAGE = "auth_image";
    /**preference声音验证key*/
    public static final String PREF_KEY_AUTH_VOICE = "auth_voice";

    /**日程推送的广播*/
    public static final String SCHEDULE_ACTION = "schedule_action";
    /**跳转到安全监控分页的action*/
    public static final String ACTION_GO_TO_SURVEILLANCE = "com.ubt.alexa.surveillance";
    /**安全监控视频广播*/
    public static final String SCHEDULE_VIDEO_ACTION = "schedule_video_action";
    /**解绑通知广播**/
    public static final String UNBIND_ROBOT_ACTION ="unbind_robot_action";

    /**
     * MainActivity finish
     */
    public static final String FINISH_MAINACTIVITY = "finish_mainactivity_action";

    /**日程开始日期*/
    public static final String PREF_SCHEDULE_BEGIN_TIME = "schedule_begin_time";
    /**日程结束日期*/
    public static final String PREF_SCHEDULE_END_TIME = "schedule_end_time";
    /**日程星期*/
    public static final String PREF_SCHEDULE_WEEKS = "schedule_weeks";

    /** 存储权限定位 **/
    public static final String PREF_PERMISSION_STORAGE="permission_storage";
    /** 存储权限定位 **/
    public static final String PREF_PERMISSION_MIC="permission_mic";
    /** 存储权限定位 **/
    public static final String PREF_PERMISSION_LOCATION="permission_location";
    /**
     * 网络监听
     * @author hjy
     * created at 2016/11/25 12:03
     */
    public class NetWork
    {
        //移动：Moblie
        public static final String MOBLIE = "Moblie";
        // Wifi:Wifi
        public static final String WIFI = "Wifi";
        //当前是wifi状态
        public static final int IS_WIFI = 0x001;
        //当前是Mobile状态
        public static final int IS_MOBILE = 0x002;
        //都没有连接
        public static final int NO_CONNECTION = 0x003;
        public static final int CONNECTED = 0x00a;
        //关闭wifi
        //public static final int OPEN_WIFI = 0x004;
        //打开wifi
        //public static final int CLOSE_WIFI = 0x005;
    }
}
