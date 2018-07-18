package com.ubtechinc.alpha.mini.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.RequestPermissionModule;
import com.ubtechinc.alpha.mini.ui.share.SharePermissionUpdateActivity;
import com.ubtechinc.alpha.mini.viewmodel.PermissionRequestModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.NotifactionView;

import java.util.List;

/**
 * @Date: 2017/12/12.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 权限工具类
 */

public class PermissionUtils {

    public static boolean checkPermission(String permissionCode) {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (currentRobot == null) {
            return false;
        }
        List<RobotPermission> permissions = currentRobot.getRobotPermissionList();
        if (currentRobot.getMasterUserId() != null && currentRobot.getMasterUserId().equals(AuthLive.getInstance().getUserId())) {
            return true;
        }
        if (CollectionUtils.isEmpty(permissions)) {
            return false;
        } else if (currentRobot.hasPermission(permissionCode)) {
            return true;
        }
        return false;
    }

    public static void requestPermission(final LifecycleOwner lifecycleOwner, String code) {
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (currentRobot == null) {
            return;
        }
        String robotUserId = currentRobot.getRobotUserId();
        final LiveResult liveResult = PermissionRequestModel.getInstance().applyPermission(robotUserId, code);
        liveResult.observe(lifecycleOwner, new Observer<LiveResult<RequestPermissionModule.Response>>() {

            @Override
            public void onChanged(@Nullable LiveResult<RequestPermissionModule.Response> result) {

                if (result.getState() == LiveResult.LiveState.SUCCESS) {
                    if (result.getData().getData().getINPERIOD() == RequestPermissionModule.PERMISSION_SEND_SUCCESS) {
                        NotifactionView notifactionView = new NotifactionView((Context) lifecycleOwner);
                        notifactionView.setNotifactionText(
                                ((Context) lifecycleOwner).getString(R.string.request_sended_control_right_tips),
                                R.drawable.ic_toast_succeed);
                    } else if (result.getData().getData().getINPERIOD() == RequestPermissionModule.PERMISSION_SEND_FREQUENT) {
                        final MaterialDialog dialog = new MaterialDialog((Context) lifecycleOwner);
                        dialog.setNegativeButton(R.string.i_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).setTitle(R.string.already_request_permission)
                                .show();
                    }
                }
            }
        });
    }

    public static void receivePermissionRequest(Context context, Message message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel(Integer.valueOf(message.getFromId()));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, message.getRobotId());

        mBuilder.setContentTitle(message.getTypeTitle())//设置通知栏标题
                //.setContentText("测试内容") //设置通知栏显示内容
                .setContentIntent(getDefalutIntent(context, message, PendingIntent.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
//	.setNumber(number) //设置通知集合的数量
                .setTicker(message.getTypeTitle()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                //.setContentInfo("123")
                .setContentInfo(context.getString(R.string.permision_setting))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_robot_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_robot_icon))
                .setContentText(message.getNoticeTitle());

        notificationManager.notify(Integer.valueOf(message.getFromId()), mBuilder.build());

    }

    private static PendingIntent getDefalutIntent(Context context, Message message, int flags) {
        Intent intent = new Intent(context, SharePermissionUpdateActivity.class);
        intent.putExtra(Constants.USERID_KEY, message.getFromId());
        intent.putExtra(Constants.USER_MESSAGE, message);
        //intent.putExtra(Constants.USERNAME_KEY, message.getSenderName());
        //intent.putExtra(Constants.RELATIONDATE_KEY, message.getTime());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);
        return pendingIntent;
    }
}
