package com.ubtechinc.alpha.mini.entity.observable;


import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.ubtechinc.alpha.mini.entity.Message;

public class MessageLive extends LiveData<MessageLive> {

    private String TAG = getClass().getSimpleName();

    private Message message;


    private static class MessageLiveHolder {
        private static MessageLive instance = new MessageLive();
    }

    private MessageLive() {
    }

    public static MessageLive get() {
        return MessageLiveHolder.instance;
    }

    private int systemMsgCount;
    private int shareMsgCount;

    public void setCount(int systemMsgCount, int shareMsgCount) {
        this.systemMsgCount = systemMsgCount;
        this.shareMsgCount = shareMsgCount;
        setValue(this);
    }

    public void receiveMessage(String commandId, Message message) {
        Log.d(TAG, "receiveMessage" + message);
        message.setCommandId(commandId);
        switch (message.getNoticeType()) {
            case Message.TYPE_SHARE:
                shareMsgCount++;
                break;
            case Message.TYPE_SYS:
                systemMsgCount++;
                break;
        }
        this.message = message;
        postValue(this);
    }

    public int getSystemMsgCount() {
        return systemMsgCount;
    }

    public int getShareMsgCount() {
        return shareMsgCount;
    }

    public int getMsgCount() {
        return systemMsgCount + shareMsgCount;
    }

    public void msgHandled() {
        this.message = null;
        setValue(this);
    }

    public Message getMessage() {
        return message;
    }

    public void clearShareCount() {
        shareMsgCount = 0;
        setValue(this);
    }

    public void clearSysCount() {
        systemMsgCount = 0;
        setValue(this);
    }

    public void readShareMsg() {
        shareMsgCount--;
        if (shareMsgCount < 0) {
            shareMsgCount = 0;
        }
        setValue(this);
    }


    public void readSysMsg() {
        systemMsgCount--;
        if (systemMsgCount < 0) {
            systemMsgCount = 0;
        }
        setValue(this);
    }
}
