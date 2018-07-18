package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Context;


import com.ubtechinc.alpha.mini.ui.PageRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/8 14:39
 * @描述: 管理启动绑定流程之前的Activity
 */

public class StoreActivityNamesBeforeBinding {

    private static final String MAIN = "MainActivity";

    private static final String SETTING_SHOW_ROBOT_WIFI = "SettingShowRobotWifiActivity";

    private static final String MINE_ROBOT_INFO = "MineRobotInfoActivity";

    private List<String> activityNames = new ArrayList<>();

    private StoreActivityNamesBeforeBinding() {

    }

    private static StoreActivityNamesBeforeBinding instance;

    public static StoreActivityNamesBeforeBinding getInstance(){
        if (instance == null){
            synchronized(StoreActivityNamesBeforeBinding.class){
                if (instance == null){
                    instance = new StoreActivityNamesBeforeBinding();
                }
            }
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }


    public void addActivityName(String activitName){
        activityNames.clear();
        activityNames.add(activitName);
    }

    public String extractActivityName(){
        if (activityNames.size() > 0){
            return activityNames.get(0);
        }else{
            return "";
        }
    }


    public void back2ActivityAccordingNames(Context ctx, String activityName){
        switch (activityName){
            case MINE_ROBOT_INFO:
                PageRouter.toMineRobotActivity(ctx);
                break;
            case SETTING_SHOW_ROBOT_WIFI:
                PageRouter.toSettingChooseWifiActivity(ctx);
                break;
            case MAIN:
            default:
                PageRouter.toMain(ctx);
                break;
        }
    }
}
