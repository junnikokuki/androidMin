package com.ubtechinc.alpha.mini.utils;

import android.app.Activity;
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
import android.widget.Toast;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.event.RequestContronRightEvent;
import com.ubtechinc.alpha.mini.ui.msg.MessageDetailActivity;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ubt on 2017/11/27.
 */

public class MsgObserveHelper {

    public static final String TAG = "MsgObserveHelper";

    MaterialDialog msgDialog;

    public void msgObserve(LifecycleOwner lifecycleOwner, final Activity activity, final MsgHandleCallback callback) {
        MessageLive.get().observe(lifecycleOwner, new Observer<MessageLive>() {
            @Override
            public void onChanged(@Nullable MessageLive messageLive) {
                Message message = messageLive.getMessage();
                Log.d(TAG, "initData " + message);
                if (message != null) {
                    switch (message.getNoticeType()) {
                        case Message.TYPE_SHARE:
                            if (String.valueOf(IMCmdId.IM_ACCOUNT_APPLY_RESPONSE).equals(message.getCommandId())
                                    && String.valueOf(Message.OP_NORMAL).equals(message.getOperation())) {
                                showShareApply(activity, message.getNoticeId(), message);
                                messageLive.msgHandled();
                            } else if (String.valueOf(IMCmdId.IM_ACCOUNT_HANDLE_APPLY_RESPONSE).equals(message.getCommandId())) {
                                String tips = "";
                                if (1 == message.getAgree()) {
                                    tips = activity.getString(R.string.agree_shared, message.getSenderName());
                                    EventBus.getDefault().post(new RequestContronRightEvent(true));
                                } else {
                                    tips = activity.getString(R.string.reject_shared, message.getSenderName());
                                    EventBus.getDefault().post(new RequestContronRightEvent(false));
                                }
                                showMsgDialog(tips, activity);
                                messageLive.msgHandled();
                            } else if (String.valueOf(IMCmdId.IM_ACCOUNT_PERMISSION_CHANGE_RESPONSE).equals(message.getCommandId())) {
                                String tips = "";
                                List<RobotPermission> permissions = message.getPermissionList();
                                if(!CollectionUtils.isEmpty(permissions)){
                                    RobotPermission permission = permissions.get(0);
                                    switch (permission.getStatus()) {
                                        case RobotPermission.STATUS_OFF:
                                            tips = activity.getString(R.string.permission_close,message.getSenderName(),permission.getPermissionDesc());
                                            break;
                                        case RobotPermission.STATUS_ON:
                                            tips = activity.getString(R.string.permission_open,message.getSenderName(),permission.getPermissionDesc());
                                            break;
                                    }
                                }
                                showMsgDialog(tips, activity);
                                messageLive.msgHandled();
                            } else if(String.valueOf(IMCmdId.IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE).equals(message.getCommandId())){
                                if(message.getToId() != null && message.getToId().equals(AuthLive.getInstance().getUserId())){
                                    showMsgDialog(message.getNoticeTitle(), activity);
                                }
                                messageLive.msgHandled();
                            } else if (String.valueOf(IMCmdId.IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE).equals(message.getCommandId())) {
                                if (message.getToId() != null && message.getFromId() != null
                                        && message.getToId().equals(AuthLive.getInstance().getUserId())) {
                                    PermissionUtils.receivePermissionRequest(activity, message);
                                }
                                messageLive.msgHandled();
                            }

                            else {
                                if (message.getFromId() == null || !message.getFromId().equals(AuthLive.getInstance().getUserId())) {
                                    Toast.makeText(activity, message.getNoticeTitle(), Toast.LENGTH_SHORT).show();
                                }
                                messageLive.msgHandled();
                            }
                            break;
                    }
                    if (callback != null) {
                        callback.handleMsg(message);
                    }
                }
            }
        });
    }

    private void showMsgDialog(String message, Activity activity) {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }
        msgDialog = new MaterialDialog(activity);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
            }
        });
        msgDialog.show();
    }

    private void showShareApply(Activity activity, String noticeId, Message message) {

        Intent intent = new Intent(activity, MessageDetailActivity.class);
        intent.putExtra(MessageDetailActivity.NOTICE_ID, noticeId);
        intent.putExtra(MessageDetailActivity.MESSAGE, message);
        intent.putExtra(MessageDetailActivity.SCENCE, MessageDetailActivity.TYPE_APPLY);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, message.getRobotId());
        mBuilder.setContentTitle(message.getTypeTitle())
                .setTicker(message.getTypeTitle())
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.DEFAULT_ALL)
                .setContentInfo(activity.getString(R.string.permision_setting))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_robot_icon)
                .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_robot_icon))
                .setContentText(message.getNoticeTitle());

        nm.notify(Integer.valueOf(message.getFromId()), mBuilder.build());

        //PageRouter.toMessageDetail(activity, noticeId, message, MessageDetailActivity.TYPE_APPLY);
    }

    public interface MsgHandleCallback {
        public void handleMsg(Message message);
    }
}
