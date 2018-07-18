package com.ubtechinc.alpha.mini.qqmusic;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.tvs.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc : QQ音乐状态实现类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/4/12
 */

public class QQMusicStatsImp extends LiveData<QQMusicStatsImp> implements IQQMusicStats, ISetQQMusicStats{

    private static final String TAG = "QQMusicStatsImp";
    private static QQMusicStatsImp instance;
    private QQMusicStatsImp() {
        // 更新礼物隐藏状态
        hidePresent = SharedPreferencesUtils.getHidePresent(AlphaMiniApplication.getInstance().getApplicationContext(), MyRobotsLive.getInstance().getRobotUserId());
        MyRobotsLive.getInstance().getCurrentRobot().observeForever(new Observer<RobotInfo>() {
            @Override
            public void onChanged(@Nullable RobotInfo robotInfo) {
                hidePresent = SharedPreferencesUtils.getHidePresent(AlphaMiniApplication.getInstance().getApplicationContext(), MyRobotsLive.getInstance().getRobotUserId());
            }
        });
    }
    private boolean hasPresent;
    private List<RobotPresentStats> robotPresentStatsList = new ArrayList<>(3);
    private ReceiveStats receiveStats = ReceiveStats.NOT_OPEN;
    private boolean isMainAccount;
    private String time = "";
    private boolean hidePresent = false;
    private volatile boolean needNotify = true;

    public static QQMusicStatsImp getInstance() {
        if(instance == null) {
            synchronized (QQMusicStatsImp.class) {
                if(instance == null) {
                    instance = new QQMusicStatsImp();
                }
            }
        }
        return instance;
    }

    public void hidePresent() {
        SharedPreferencesUtils.putHidePresent(AlphaMiniApplication.getInstance().getApplicationContext(), MyRobotsLive.getInstance().getRobotUserId());
        this.hidePresent = true;
    }

    public void setNeedNotify(boolean needNotify) {
        this.needNotify = needNotify;
    }

    @Override
    public boolean hasPresent() {
        return !hidePresent && hasPresent;
    }

    @Override
    public List<RobotPresentStats> getRobotPresentStatss() {
        Log.i(TAG, " getRobotPresentStatss -- robotPresentStatsList" + robotPresentStatsList);
        return robotPresentStatsList;
    }

    @Override
    public ReceiveStats getReceiveStats() {
        return receiveStats;
    }

    @Override
    public boolean isMainAccount() {
        // TODO 测试设置为true
        Log.i(TAG, " isMainAccount : " + isMainAccount);
        return isMainAccount;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void isHasPresent(boolean hasPresent) {
        Log.i(TAG, " isHasPresent hasPresent : " + hasPresent + "this.hasPresent : " + this.hasPresent);
        if(this.hasPresent != hasPresent) {
            this.hasPresent = hasPresent;
            update();
        }
    }

    private void update() {
        if(needNotify) {
            setValue(this);
        }
    }

    @Override
    public void setRobotPresentStatss(List<RobotPresentStats> robotPresentStatsList) {
        Log.e(TAG, "robotPresentStatsList : " + robotPresentStatsList);
        this.robotPresentStatsList.clear();
        if(robotPresentStatsList != null) {
            this.robotPresentStatsList.addAll(robotPresentStatsList);
        }
        update();
    }

    @Override
    public void setReceiveStats(ReceiveStats receiveStats) {
        Log.i(TAG, " setReceiveStats ReceiveStats : " + receiveStats);
        if(!this.receiveStats.equals(receiveStats)) {
            this.receiveStats = receiveStats;
            update();
        }
    }

    @Override
    public void setIsMainAccount(boolean isMainAccount) {
        Log.i(TAG, " setIsMainAccount isMainAccount : " + isMainAccount + " this.isMainAccount : " + this.isMainAccount);
        if(this.isMainAccount != isMainAccount) {
            this.isMainAccount = isMainAccount;
            hidePresent = SharedPreferencesUtils.getHidePresent(AlphaMiniApplication.getInstance().getApplicationContext(), MyRobotsLive.getInstance().getRobotUserId());
            update();
        }
    }

    @Override
    public void setTime(String time) {
        String[] strings = time.split(" ");
        if(strings.length == 2) {
            time = strings[0];
        }
        if(!this.time.equals(time)) {
            this.time = time;
            update();
        }
    }

    public void reset() {
        setReceiveStats(ReceiveStats.NOT_OPEN);
        setTime("");
        setIsMainAccount(false);
        setRobotPresentStatss(null);
    }
}
