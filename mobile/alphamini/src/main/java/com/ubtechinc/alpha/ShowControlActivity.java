package com.ubtechinc.alpha;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubt.lancommunicationhelper.LanCommunicationProxy;
import com.ubt.lancommunicationhelper.MessageListener;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.thread.HandlerUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtrobot.lib.robot.proto.ProtoClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob.xu on 2018/1/8.
 */

public class ShowControlActivity extends Activity {

    ArrayAdapter<String> adapter;
    public static final String TAG = "ShowControlActivity";
    private String currBehaviorName;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_control);
        setupBehaviorSpinner();

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                stopBehavior();
            }
        });
        GetRobotIp.GetIpRequest request = GetRobotIp.GetIpRequest.newBuilder().setPath("").build();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_ROBOT_IP_REQUEST, IMCmdId.IM_VERSION, request,
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<GetRobotIp.GetIpResponse>() {

                    @Override
                    public void onSuccess(GetRobotIp.GetIpResponse response) {
                        if (response != null) {
                            final String robotIP = response.getIp();
                            final String robotSSID = response.getWifissid();
                            LogUtils.d(TAG,"GetIpResponse----robotIp:"+robotIP+", robotSSID :"+robotSSID);

                            WifiManager wifiManager = (WifiManager) ShowControlActivity.this.getSystemService(Context.WIFI_SERVICE);
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            String mobileSSID = "";
                            if (wifiInfo != null) {
                                mobileSSID = wifiInfo.getSSID();
                            }
                            if (robotSSID != null && robotSSID.equals(mobileSSID)) {
                                LanCommunicationProxy.getInstance(ShowControlActivity.this).connect2Robot(robotIP);
                                HandlerUtils.getMainHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ShowControlActivity.this,"开始连接机器人："+robotIP,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                HandlerUtils.getMainHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ShowControlActivity.this,"机器人与手机不在相同的路由器，robotSSID:"+robotSSID,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {

                    }
        });


        LanCommunicationProxy.getInstance(this).registerMessaggeListener(new MessageListener() {

            @Override
            public void onReceiveMessage(PushMessage.MessageWrapper messageWrapper) {
                String messageId = messageWrapper.getMessageId();
                Any any = messageWrapper.getBody();
                String text = null;
                try {
                    ProtoClass.ProtoText protoText = any.unpack(ProtoClass.ProtoText.class);
                    text = protoText.getText();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                LogUtils.d(TAG,"onReceiveMessage---messageId = "+messageId+", text = "+text);
            }
        });
    }

    private void setupBehaviorSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final List<String> fileNames = new ArrayList<>();
        fileNames.add("选择表现力文件");
        adapter.addAll(fileNames);
        new Thread(new Runnable() {
            @Override public void run() {

                GetBehaviorList.GetListRequest request = GetBehaviorList.GetListRequest.newBuilder().setPath("robotshow").build();
                Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_BEHAVIOR_LIST_REQUEST,IMCmdId.IM_VERSION,request,MyRobotsLive.getInstance().getRobotUserId(),
                        new ICallback<GetBehaviorList.GetListResponse>() {

                            @Override
                            public void onSuccess(GetBehaviorList.GetListResponse response) {
                                LogUtils.d(TAG,"getBehaviorList --succ");
                                final List<String> behaviorList = response.getBehaviorListList();

                                HandlerUtils.getMainHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addAll(behaviorList);
                                    }
                                });

                            }

                            @Override
                            public void onError(ThrowableWrapper e) {
                                LogUtils.d(TAG,"getBehaviorList --fail");
                            }
                        });

            }
        }).start();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                String fileName = adapter.getItem(position);
                currBehaviorName = fileName;
                playBehavior(fileName);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void playBehavior(String behaviorName) {
        AIPlayBehevior.BehaviorRequest behaviorRequest = AIPlayBehevior.BehaviorRequest.newBuilder()
                .setBehaviorName(behaviorName).setAction("play").build();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_PLAY_BEHAVIOR_REQUEST, IMCmdId.IM_VERSION, behaviorRequest, MyRobotsLive.getInstance().getRobotUserId(), new ICallback<AIPlayBehevior.BehaviorResponse>() {

            @Override
            public void onSuccess(AIPlayBehevior.BehaviorResponse response) {
                LogUtils.d(TAG, "send--play_behavior--succ");
            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG, "send--play_behavior--fail");
            }
        });
    }

    public void stopBehavior() {
        AIPlayBehevior.BehaviorRequest behaviorRequest = AIPlayBehevior.BehaviorRequest.newBuilder()
                .setBehaviorName(currBehaviorName).setAction("stop").build();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_PLAY_BEHAVIOR_REQUEST,IMCmdId.IM_VERSION,behaviorRequest, MyRobotsLive.getInstance().getRobotUserId(),new ICallback<AIPlayBehevior.BehaviorResponse>() {

            @Override
            public void onSuccess(AIPlayBehevior.BehaviorResponse response) {

                LogUtils.d(TAG,"send--play_behavior--succ");
            }

            @Override
            public void onError(ThrowableWrapper e) {
                LogUtils.d(TAG,"send--play_behavior--fail");
            }
        });

    }



}
