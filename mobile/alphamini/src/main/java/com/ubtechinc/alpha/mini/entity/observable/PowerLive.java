package com.ubtechinc.alpha.mini.entity.observable;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.PowerValue;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.repository.RobotPowerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ubt on 2018/3/14.
 */

public class PowerLive extends MediatorLiveData<PowerLive> {

    public static final String TAG = "PowerLive";

    private static PowerLive sInstance;

    private Timer timer;

    private TimerTask powerTask;

    private RobotPowerRepository powerRepository;

    private Map<String, PowerValue> powerValueMap;

    private NetworkHelper.NetworkInductor networkInductor;

    public static PowerLive get() {
        if (sInstance == null) {
            synchronized (PowerLive.class) {
                if (sInstance == null) {
                    sInstance = new PowerLive();
                }
            }
        }
        return sInstance;
    }

    private PowerLive() {
        timer = new Timer();
        powerRepository = new RobotPowerRepository();
        powerValueMap = new HashMap<>();
    }

    /**
     * 监听当前机器人的状态
     */
    private void addSource() {
        Log.d(TAG, "addSource");
        addSource(MyRobotsLive.getInstance().getCurrentRobot(), new Observer<RobotInfo>() {

            @Override
            public void onChanged(@Nullable RobotInfo robotInfo) {
                LogUtils.d(TAG, "onChanged " + robotInfo);
                if (robotInfo == null) {
                    stop();
                    clearData();
                    return;
                }
                Log.d(TAG, "onChanged " + robotInfo.getOnlineState());
                switch (robotInfo.getOnlineState()) {
                    case RobotInfo.ROBOT_STATE_ONLINE:
                        start();
                        break;
                    default:
                        stop();
                        clearData();
                        break;
                }
            }
        });
        addNetworkListener();
    }

    private void removeSource() {
        Log.d(TAG, "removeSource");
        removeSource(MyRobotsLive.getInstance().getCurrentRobot());
        removeNetworkListener();
    }

    @Override
    protected void onActive() {
        super.onActive();
        Log.d(TAG, "onActive");
        addSource();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.d(TAG, "onInactive");
        removeSource();
        stop();
    }

    private void start() {
        LogUtils.d(TAG, "start " + powerTask);
        postValue(PowerLive.this);
        if (powerTask == null) {
            powerTask = new TimerTask() {
                @Override
                public void run() {
                    getPowerValue();
                }
            };
            timer.schedule(powerTask, 0, 60000);
        } else {
            if (powerValueMap.get(MyRobotsLive.getInstance().getRobotUserId()) == null) {
                powerTask.cancel();
                powerTask = new TimerTask() {
                    @Override
                    public void run() {
                        getPowerValue();
                    }
                };
                timer.schedule(powerTask, 0, 60000);
            }
        }
    }

    private void stop() {
        Log.d(TAG, "stop");
        if (powerTask != null) {
            powerTask.cancel();
            powerTask = null;
        }
//        timer.cancel();
    }

    private void clearData() {
        Log.d(TAG, "clearData");
//        postValue(null);
        postValue(this);
    }

    private void getPowerValue() {
        Log.d(TAG, "getPowerValue");
        String robotId = null;
        RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (robotInfo != null && robotInfo.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            robotId = robotInfo.getRobotUserId();
        }
        Log.d(TAG, "currentRobotId = " + robotId);
        if (robotId == null) {
            return;
        }
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            return;
        }
        final String finalRobotId = robotId;
        Log.d(TAG, "real getPower");
        powerRepository.getPower(robotId, new RobotPowerRepository.GetPowerCallback() {
            @Override
            public void onSuccess(PowerValue powerValue) {
                powerValueMap.put(finalRobotId, powerValue);
                postValue(PowerLive.this);
            }

            @Override
            public void onError() {

            }
        });
    }

    public PowerValue getCurrentValue() {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (currentRobot == null || currentRobot.getOnlineState() != RobotInfo.ROBOT_STATE_ONLINE) {
            return null;
        }
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            return null;
        }
        return powerValueMap.get(MyRobotsLive.getInstance().getRobotUserId());
    }

    public PowerValue getRobotValue(String robotId) {
        return powerValueMap.get(robotId);
    }

    public boolean isLowPower() {
        PowerValue powerValue = powerValueMap.get(MyRobotsLive.getInstance().getRobotUserId());
        return powerValue != null ? powerValue.isLowPower : false;
    }

    private void addNetworkListener() {
        initNetworkInductor();
        NetworkHelper.sharedHelper().registerNetworkSensor(AlphaMiniApplication.getInstance());
        NetworkHelper.sharedHelper().addNetworkInductor(networkInductor);
    }

    private void removeNetworkListener() {
        NetworkHelper.sharedHelper().removeNetworkInductor(networkInductor);
    }

    private void initNetworkInductor() {
        if (networkInductor == null) {
            networkInductor = new NetworkHelper.NetworkInductor() {
                @Override
                public void onNetworkChanged(NetworkHelper.NetworkStatus status) {
                    LogUtils.d(TAG, "onNetworkChanged----net available :" + NetworkHelper.sharedHelper().isNetworkAvailable());
                    if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
                        stop();
                        clearData();
                    } else {
                        if (isRobotOnline()) {
                            start();
                        }
                    }
                }
            };
        }
    }

    private boolean isRobotOnline() {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (currentRobot != null && currentRobot.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            return true;
        }
        return false;
    }

}
