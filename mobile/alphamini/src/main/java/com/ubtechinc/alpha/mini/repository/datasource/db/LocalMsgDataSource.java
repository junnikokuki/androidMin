package com.ubtechinc.alpha.mini.repository.datasource.db;


import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.repository.datasource.IMsgDataSource;
import com.ubtechinc.alpha.mini.database.MessageProvider;

import java.util.List;

public class LocalMsgDataSource implements IMsgDataSource {

    public static final String TAG = "LocalMsgDataSource";

    @Override
    public void getMsgs(String userId, int type, int page, int pagesize, GetMsgsCallback callback) {
        Log.d(TAG, "getMsgs");
        List<Message> messages = MessageProvider.get().getByUserIdAndType(userId, type, (page - 1) * pagesize, pagesize);
        Log.d(TAG, "getMsgs result = " + (CollectionUtils.isEmpty(messages) ? 0 : messages.size()));
        if (callback != null) {
            if (type == Message.TYPE_SYS) {
                callback.onSuccess(messages, null);
            } else {
                callback.onSuccess(null, messages);
            }
        }
    }

    @Override
    public void getMsg(String noticeId, GetMsgCallback callback) {
        Log.d(TAG, "getMsg id=" + noticeId);
        Message msg = MessageProvider.get().getByNoticeId(noticeId);
        if (msg != null) {
            if (callback != null) {
                callback.onSuccess(msg);
            }
        } else {
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
    }

    public void expiredMsg(String robotId, String userId, String cmId) {
        MessageProvider.get().expiredMsg(userId, robotId, cmId);
    }

    public void save(List<Message> messages) {
        MessageProvider.get().saveOrUpdateAll(messages);
    }

    public void save(Message message) {
        MessageProvider.get().saveOrUpdate(message);
    }

    public void clearAll(int type) {
        MessageProvider.get().deleteByType(type);
    }

    @Override
    public void getNoReadCount(final GetNoReadCountCallback callback) {

    }

    @Override
    public void readMsg(String noticeId, ReadMsgCallback callback) {
        MessageProvider.get().readMsg(noticeId);
        if(callback != null){
            callback.onSuccess();
        }
    }

    @Override
    public void readAllMsg(String userId, int type, ReadMsgCallback callback) {
        MessageProvider.get().readAllMsg(userId, type);
        if(callback != null){
            callback.onSuccess();
        }
    }

}
