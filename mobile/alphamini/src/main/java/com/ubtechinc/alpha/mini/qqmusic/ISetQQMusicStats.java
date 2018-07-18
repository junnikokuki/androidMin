package com.ubtechinc.alpha.mini.qqmusic;

import java.util.List;

/**
 * @author htoall
 * @Description:
 * @date 2018/4/18 下午8:05
 * @copyright TCL-MIE
 */
public interface ISetQQMusicStats {
    void isHasPresent(boolean hasPresent);
    void setRobotPresentStatss(List<RobotPresentStats> robotPresentStatsList);
    void setReceiveStats(ReceiveStats receiveStats);
    void setIsMainAccount(boolean isMainAccount);
    void setTime(String time);
}
