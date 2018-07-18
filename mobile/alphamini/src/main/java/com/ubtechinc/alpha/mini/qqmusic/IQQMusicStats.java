package com.ubtechinc.alpha.mini.qqmusic;

import java.util.List;

/**
 * @desc : QQ音乐状态接口
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/12
 */

public interface IQQMusicStats {

    // 当前账号是否有礼物
    boolean hasPresent();

    // 获取作为主账号的机器人列表领取状态
    List<RobotPresentStats> getRobotPresentStatss();

    // 获取领取状态
    ReceiveStats getReceiveStats();

    // 当前是否是主账号
    boolean isMainAccount();

    // 到期时间
    String getTime();

    // 是否需要更新
    void setNeedNotify(boolean needNotify);
}
