package com.ubtect.xingepushlibrary;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date: 2017/11/2.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UbtXinGeMessageReceiver extends XGPushBaseReceiver {

    private final String TAG = getClass().getSimpleName();

    /**
     * 通知id生成器
     */
    private static AtomicInteger sIdGenerator = new AtomicInteger(0);

    private static final int MAX_NOTIFICATION_SIZE = 5;

    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        Log.d(TAG, "[onRegisterResult]");
        if (context == null || message == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            // 在这里拿token
            String token = message.getToken();
            text = message + "注册成功, token : " + token;
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        Log.d(TAG, "[onUnregisterResult]");
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        Log.d(TAG, "[onSetTagResult]");
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        Log.d(TAG, "[onDeleteTagResult]");
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        Log.d(TAG, "[onTextMessage] " + message.toString());

        // 获取自定义key-value
        String customContent = message.getCustomContent()/*message.getContent()*/;
        if (customContent != null && customContent.length() != 0) {
            Log.d(TAG, "customContent : " + customContent);
        }
        String content = message.getContent();
        if (!TextUtils.isEmpty(content)){
            UbtXinGeManager.getInstance().onReceverMessage(customContent);
        }

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.d(TAG, "[onNotifactionClickedResult]");
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.d(TAG, "[onNotifactionShowedResult]");
    }
}
