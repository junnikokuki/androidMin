package com.ubtechinc.alpha.mini.avatar;

import com.ubtechinc.alpha.mini.avatar.viewutils.BottomLayUtil;

/**
 * @作者：liudongyang
 * @日期: 18/5/21 18:34
 * @描述:
 */

public interface AvatarControlListener extends BottomLayUtil.OnBottomBtnClickListener {

    public void onAction(String action);

    public void onHeaderControl(int direction);

    public void onWalkControl(int direction);

}
