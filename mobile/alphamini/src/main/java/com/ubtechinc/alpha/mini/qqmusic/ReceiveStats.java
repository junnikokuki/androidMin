package com.ubtechinc.alpha.mini.qqmusic;

import android.support.annotation.StringRes;

import com.ubtechinc.alpha.mini.R;

/**
 * @desc : 领取状态
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/12
 */

public enum ReceiveStats {
    // 马上领取
    RECEIVE_NOW(R.string.receive_now),
    // 继续领取
    RECEIVE_CONTINUE(R.string.receive_continue),
    // 已开通
    ALREADY_OPEN(R.string.renew),
    // 已过期
    ALREADY_OUTTIME(R.string.already_outtime),
    // 未开通
    NOT_OPEN(R.string.unopen);

    private @StringRes int resId;

    ReceiveStats(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
