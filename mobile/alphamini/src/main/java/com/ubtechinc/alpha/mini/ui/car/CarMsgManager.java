package com.ubtechinc.alpha.mini.ui.car;

import android.util.Log;

import com.ubtechinc.alpha.JimuCarCheck;
import com.ubtechinc.alpha.JimuCarConnectBleCar;
import com.ubtechinc.alpha.JimuCarControl;
import com.ubtechinc.alpha.JimuCarDriveMode;
import com.ubtechinc.alpha.JimuCarGetBleList;
import com.ubtechinc.alpha.JimuCarGetIRDistance;
import com.ubtechinc.alpha.JimuCarLevelOuterClass;
import com.ubtechinc.alpha.JimuCarListenType;
import com.ubtechinc.alpha.JimuCarPower;
import com.ubtechinc.alpha.JimuCarRobotChat;
import com.ubtechinc.alpha.JimuErrorCodeOuterClass;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.CarListInfo;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.alpha.mini.event.JimuFindCarListEvent;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by riley.zhang on 2018/7/3.
 */

public class CarMsgManager {

    private static final int TIMER_PERIOD = 3000;

    public static final int ENTER_DRIVE_MODE = 0;
    public static final int EXIT_DRIVE_MODE = 1;

    public static final int JIMU_CAR_FORWARD = 0;//小车向前走
    public static final int JIMU_CAR_BACK = 1;//小车向后退
    public static final int JIMU_CAR_LEFT = 2;//小车左转弯
    public static final int JIMU_CAR_RIGHT = 3;//小车右转弯
    public static final int JIMU_CAR_RESET_DIRECTION = 4;// 打正方向
    public static final int JIMU_CAR_RIGHT_BACK = 5;// 预留
    public static final int JIMU_CAR_BELL_HORN = 6;//预留
    public static final int JIMU_CAR_FLASHING_LIGHT = 7;// 预留
    public static final int JIMU_CAR_STOP = 8;// 停止(适用松手的时候  之前一直按着向前进，然后松手的时候发这个指令)

    public static final int RESET_CHAT_MODE = 0;
    public static final int CUSTOM_CHAT_MODE = 1;

    public static final int START_SEARCH_BLE = 0;
    public static final int END_SEARCH_BLE = 1;

    public static final int NOMAL_MODE = 1;
    public static final int POLICE_MODE = 2;
    public static ArrayList<CarListInfo> mCarList = new ArrayList<>();
    private static Timer mTimer;
    private static MyTask mRunTask;

    private String mDriveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();


    public void setDriveCarUsedId(String userId) {
        mDriveCarUsedId = userId;
    }

    //进入或退出开车模式
    /**
     *cmdId:  ENTER_DRIVE_MODE -enter  EXIT_DRIVE_MODE -exit
     * */
    public static void driveModeCmd(int cmdId) {
        Log.i("test", "driveModeCmd cmdId = " + cmdId);
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarDriveMode.ChangeJimuDriveModeRequest.Builder builder = JimuCarDriveMode.ChangeJimuDriveModeRequest.newBuilder();
        JimuCarDriveMode.DriveMode driveMode = JimuCarDriveMode.DriveMode.ENTER;
        JimuCarListenType.listenType listenType = JimuCarListenType.listenType.ALWAYS;
        if (cmdId == EXIT_DRIVE_MODE) {
            driveMode = JimuCarDriveMode.DriveMode.QUIT;
            listenType = JimuCarListenType.listenType.ONCE;
        }
        builder.setDriveMode(driveMode);
        builder.setListenType(listenType);

        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CHANGE_DRIVE_MODE_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarDriveMode.ChangeJimuDriveModeResponse>() {
            @Override
            public void onSuccess(JimuCarDriveMode.ChangeJimuDriveModeResponse response) {
                if (response.getDriveMode() == JimuCarDriveMode.DriveMode.ENTER &&
                        response.getErrorCode() == JimuErrorCodeOuterClass.JimuErrorCode.REQUEST_SUCCESS) {
                    if (response.getState() == JimuCarConnectBleCar.BleCarConnectState.CONNECTED) {
                        //TODO
                        Log.i("test", "connected success send event");
                    }else if (response.getBleCount() > 1) {
                        List<JimuCarGetBleList.JimuCarBle> carBle = response.getBleList();
                        mCarList.clear();
                        for (int i = 0; i < carBle.size(); i++) {
                            CarListInfo car = new CarListInfo();
                            Log.i("test", "getMac = " + carBle.get(i).getMac() + " getName = " + carBle.get(i).getName());
                            car.setDeviceMac(carBle.get(i).getMac());
                            car.setDeviceBleName(carBle.get(i).getName());
                            mCarList.add(car);
                        }
                        EventBus.getDefault().post(new JimuFindCarListEvent());
                    }
                }
                Log.i("test", "IM_DRIVE_MODE onSuccess response = " + response);
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "IM_DRIVE_MODE onError");
            }
        });
    }

    //控制小车指令（前进/后退/左转弯/右转弯/停止）
    /**
     *cmdId:    JIMU_CAR_FORWARD        前进
     *          JIMU_CAR_BACK           后退
     *          JIMU_CAR_LEFT_FORWARD   左转弯
     *          JIMU_CAR_RIGHT_FORWARD  右转弯
     *          JIMU_CAR_STOP           停止
     * */
    public static void jimuCarControlCmd(int cmdId) {
        JimuCarControl.Control controlId = JimuCarControl.Control.CAR_FORWARD;
        switch(cmdId) {
            case JIMU_CAR_STOP:
                controlId = JimuCarControl.Control.CAR_STOP;
                break;
            case JIMU_CAR_FORWARD:
                controlId = JimuCarControl.Control.CAR_FORWARD;
                break;
            case JIMU_CAR_BACK:
                controlId = JimuCarControl.Control.CAR_BACK;
                break;
            case JIMU_CAR_LEFT:
                controlId = JimuCarControl.Control.CAR_LEFT;
                break;
            case JIMU_CAR_RIGHT:
                controlId = JimuCarControl.Control.CAR_RIGHT;
                break;
            case JIMU_CAR_RESET_DIRECTION:
                controlId = JimuCarControl.Control.CAR_RESET_DIRECTION;
                break;
            default:
                break;
        }

        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarControl.JimuCarControlRequest.Builder mBuilder = JimuCarControl.JimuCarControlRequest.newBuilder();
        mBuilder.setCmd(controlId);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CONTROL_REQUEST, IMCmdId.IM_VERSION, mBuilder.build(), driveCarUsedId, new ICallback<JimuCarControl.JimuCarControlResponse>(){
            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "JIMU_CAR_CONTROL onError");
            }

            @Override
            public void onSuccess(JimuCarControl.JimuCarControlResponse response) {
                Log.i("test", "JIMU_CAR_CONTROL onSuccess " + response);
            }
        });
    }

    private static void timerControl(int cmdId) {
        Log.i("test", "timerControl cmdId = " + cmdId);
        JimuCarControl.Control controlId = JimuCarControl.Control.CAR_FORWARD;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        switch (cmdId) {
            case JIMU_CAR_FORWARD:
                controlId = JimuCarControl.Control.CAR_FORWARD;
                break;
            case JIMU_CAR_BACK:
                controlId = JimuCarControl.Control.CAR_BACK;
                break;
            default:
                break;
        }
        if (mRunTask == null) {
            mRunTask = new MyTask(controlId);
        } else {
            mRunTask.cancel();
            mRunTask = new MyTask(controlId);
        }
        mTimer.schedule(mRunTask, 0, TIMER_PERIOD);
    }

    private static class MyTask extends TimerTask{

        String driveCarUsedId;
        JimuCarControl.JimuCarControlRequest.Builder mBuilder;

        protected MyTask(JimuCarControl.Control control) {
            driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
            mBuilder = JimuCarControl.JimuCarControlRequest.newBuilder();
            mBuilder.setCmd(control);
        }

        @Override
        public void run() {
            Log.i("test", "MyTask IM_JIMU_CAR_CONTROL_REQUEST cmd = " + mBuilder.getCmd());
            Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CONTROL_REQUEST, IMCmdId.IM_VERSION, mBuilder.build(), driveCarUsedId, new ICallback<JimuCarControl.JimuCarControlResponse>(){
                @Override
                public void onError(ThrowableWrapper e) {
                    Log.i("test", "JIMU_CAR_CONTROL onError");
                }

                @Override
                public void onSuccess(JimuCarControl.JimuCarControlResponse response) {
                    Log.i("test", "JIMU_CAR_CONTROL onSuccess " + response);
                }
            });
        }
    }

    //获取小车红外距离
    public static void getCarIrDistance() {
        Log.i("test", "getCarIrDistance");
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarGetIRDistance.JimuCarGetIRDistanceRequest.Builder builder = JimuCarGetIRDistance.JimuCarGetIRDistanceRequest.newBuilder();
        builder.setListenType(JimuCarListenType.listenType.ALWAYS);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_GET_IR_DISTANCE_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarGetIRDistance.JimuCarGetIRDistanceResponse>(){
            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "getCarIrDistance onError");

            }

            @Override
            public void onSuccess(JimuCarGetIRDistance.JimuCarGetIRDistanceResponse response) {
                Log.i("test", "getCarIrDistance onSuccess response.toString() = " + response);
            }
        });
    }

    //控制机器人指令（播放某一条指令、警笛、喇叭）
    /**
     * cmdId:       RESET_CHAT_MODE
     *              RESET_CHAT_MODE
     *
     * chatContent:
     * */
    public static void robotControlCmd(int cmdId, String chatContent) {
        Log.i("test", "robotControlCmd");
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarRobotChat.JimuCarRobotChatRequest.Builder builder = JimuCarRobotChat.JimuCarRobotChatRequest.newBuilder();
        JimuCarRobotChat.chatMode chatMode = JimuCarRobotChat.chatMode.PRESET;
        if (cmdId == CUSTOM_CHAT_MODE) {
            chatMode = JimuCarRobotChat.chatMode.CUSTOM;
        }
        builder.setChatMode(chatMode);
        builder.setContent(chatContent);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_ROBOT_CHAT_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarRobotChat.JimuCarRobotChatResponse>() {
            @Override
            public void onSuccess(JimuCarRobotChat.JimuCarRobotChatResponse response) {
                Log.i("test", "robotControlCmd onSuccess response.toString() = " + response.toString());
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "robotControlCmd onError");
            }
        });
    }

    //开始停止搜索小车蓝牙
    /**
     * cmdId:   START_SEARCH_BLE    开始搜索
     *          END_SEARCH_BLE      停止搜索
     *
     * */
    public static void searchJimuCarBleCmd(int cmdId, final CarModeInterface.ICarListListener carListListener) {
        Log.i("test", " searchJimuCarBleCmd");
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarGetBleList.GetJimuCarBleListRequest.Builder builder = JimuCarGetBleList.GetJimuCarBleListRequest.newBuilder();
        JimuCarListenType.listenType listenType = JimuCarListenType.listenType.ALWAYS;
        builder.setListenType(listenType);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_GET_BLE_CAR_LIST_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarGetBleList.GetJimuCarBleListResponse>() {
            @Override
            public void onSuccess(JimuCarGetBleList.GetJimuCarBleListResponse response) {
                Log.i("test", "searchJimuCarBleCmd onSuccess BleList = " + response.getBleList().size());
                carListListener.onSuccess(response);
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "searchJimuCarBleCmd onError e = " + e);
                carListListener.onFail(e);
            }
        });
    }

    //要连接指定的小车蓝牙指令
    /**
     * macAddress:  要连接的小车的mac名字（机器人会把小车的ble名字和mac名字一个发给客户端）
     *
     * */
    public static void connectBleCarCmd(String macAddress, final CarModeInterface.ICarConnectedListener connectedListener) {
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarConnectBleCar.JimuCarConnectBleCarRequest.Builder builder = JimuCarConnectBleCar.JimuCarConnectBleCarRequest.newBuilder();
        builder.setMac(macAddress);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CONNECT_CAR_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId, new ICallback<JimuCarConnectBleCar.JimuCarConnectBleCarResponse>() {
            @Override
            public void onSuccess(JimuCarConnectBleCar.JimuCarConnectBleCarResponse response) {
                Log.i("test", "connectBleCarCmd onSuccess response = " + response);
                connectedListener.onSuccess();
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "connectBleCarCmd onError");
                connectedListener.onFail();
            }
        });
    }

    //检查零件指令

    public static void checkJimuCarPartCmd() {
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarCheck.checkCarRequest.Builder builder = JimuCarCheck.checkCarRequest.newBuilder();
        builder.setListenType(JimuCarListenType.listenType.ALWAYS);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CHECK_REQUEST, IMCmdId.IM_VERSION, null, driveCarUsedId,
                new ICallback<JimuCarCheck.checkCarResponse>() {
            @Override
            public void onSuccess(JimuCarCheck.checkCarResponse response) {
                Log.i("test", "checkJimuCarPartCmd onSuccess response = " + response);
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.i("test", "checkJimuCarPartCmd onError");
            }
        });
    }

    public static void getJimuCarPower(final CarModeInterface.ICarPowerListener listener) {
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarPower.GetJimuCarPowerRequest.Builder builder = JimuCarPower.GetJimuCarPowerRequest.newBuilder();
        builder.setListenType(JimuCarListenType.listenType.ALWAYS);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_QUERY_POWER_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarPower.GetJimuCarPowerResponse>() {
                    @Override
                    public void onSuccess(JimuCarPower.GetJimuCarPowerResponse response) {
                        Log.i("test", "getJimuCarPower onSuccess response = " + response);
                        listener.onSuccess(response);
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.i("test", "getJimuCarPower onError");
                        listener.onFail(e);
                    }
                });
    }

    public static void changeDriveMode(int driveMode) {
        String driveCarUsedId = MyRobotsLive.getInstance().getCurrentRobot().getValue().getRobotUserId();
        JimuCarLevelOuterClass.ChangeJimuCarLevelRequest.Builder builder = JimuCarLevelOuterClass.ChangeJimuCarLevelRequest.newBuilder();
        JimuCarLevelOuterClass.JimuCarLevel carLevel = JimuCarLevelOuterClass.JimuCarLevel.NORMAL;
        if (driveMode == POLICE_MODE) {
            carLevel = JimuCarLevelOuterClass.JimuCarLevel.POLICE;
        }
        builder.setLevel(carLevel);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_JIMU_CAR_CHANGE_LEVEL_REQUEST, IMCmdId.IM_VERSION, builder.build(), driveCarUsedId,
                new ICallback<JimuCarLevelOuterClass.ChangeJimuCarLevelResponse>() {
                    @Override
                    public void onSuccess(JimuCarLevelOuterClass.ChangeJimuCarLevelResponse response) {
                        Log.i("test", "changeDriveMode onSuccess response = " + response);
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.i("test", "changeDriveMode onError");
                    }
                });
    }
}
