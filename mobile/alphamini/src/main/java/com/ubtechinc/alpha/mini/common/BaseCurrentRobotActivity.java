package com.ubtechinc.alpha.mini.common;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

public abstract class BaseCurrentRobotActivity extends BaseToolbarActivity {

    protected RobotInfo currentInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        MyRobotsLive.getInstance().getCurrentRobot().observe(this, new Observer<RobotInfo>() {
            @Override
            public void onChanged(@Nullable RobotInfo robotInfo) {
                if (robotInfo == null) {
                    finish();
                } else {
                    if (currentInfo == null) {
                        currentInfo = robotInfo;
                        onCurrentChange(currentInfo);
                    } else {
                        if (currentInfo.getRobotUserId().equals(robotInfo.getRobotUserId())) {
                            onCurrentChange(currentInfo);
                        } else {
                            finish();
                        }
                    }
                }
            }
        });
    }

    protected abstract void onCurrentChange(RobotInfo robotInfo);

}
