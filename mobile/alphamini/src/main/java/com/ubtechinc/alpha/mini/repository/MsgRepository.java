package com.ubtechinc.alpha.mini.repository;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.repository.datasource.IMsgDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.db.LocalMsgDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.server.RemoteMsgDataSource;

import java.util.List;

/**
 * 消息数据获取.
 */

public class MsgRepository {

    private IMsgDataSource remoteMsgDataSource;
    private LocalMsgDataSource localMsgDataSource;

    public final static int DEFAULT_PAGE_SIZE = 20;

    public MsgRepository() {
        remoteMsgDataSource = new RemoteMsgDataSource();
        localMsgDataSource = new LocalMsgDataSource();
    }

    public void getNoReadMsgCount(IMsgDataSource.GetNoReadCountCallback getNoReadCountCallback) {
        remoteMsgDataSource.getNoReadCount(getNoReadCountCallback);
    }

    public void getSysMessageFromDB(String userId, int page, final IMsgDataSource.GetMsgsCallback callback) {
        localMsgDataSource.getMsgs(userId, page, Message.TYPE_SYS, DEFAULT_PAGE_SIZE, callback);
    }

    public void getSysMessages(String userId, int page, final IMsgDataSource.GetMsgsCallback callback) {
        remoteMsgDataSource.getMsgs(userId, Message.TYPE_SYS, page, DEFAULT_PAGE_SIZE, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                if (callback != null) {
                    if (!CollectionUtils.isEmpty(sysMsgs)) {
                        localMsgDataSource.save(sysMsgs);
                    }
                    if (!CollectionUtils.isEmpty(shareMsgs)) {
                        localMsgDataSource.save(shareMsgs);
                    }
                    callback.onSuccess(sysMsgs, shareMsgs);
                }
            }

            @Override
            public void onError() {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }

    public void getShareMessageFromDB(final String userId, final int page, final IMsgDataSource.GetMsgsCallback callback) {
        localMsgDataSource.getMsgs(userId, Message.TYPE_SHARE, page, DEFAULT_PAGE_SIZE, callback);
    }

    public void getShareMessage(final String userId, final int page, final IMsgDataSource.GetMsgsCallback callback) {
        remoteMsgDataSource.getMsgs(userId, Message.TYPE_SHARE, page, DEFAULT_PAGE_SIZE, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                if (callback != null) {
                    if (!CollectionUtils.isEmpty(sysMsgs)) {
                        localMsgDataSource.save(sysMsgs);
                    }
                    if (!CollectionUtils.isEmpty(shareMsgs)) {
                        localMsgDataSource.save(shareMsgs);
                    }
                    callback.onSuccess(sysMsgs, shareMsgs);
                }
            }

            @Override
            public void onError() {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }


    public void getMessage(String noticeId, IMsgDataSource.GetMsgCallback callback) {
        localMsgDataSource.getMsg(noticeId, callback);
    }

    public void save(Message message) {
        localMsgDataSource.save(message);
    }

    public void expiredMsg(String userId, String robotId, String commandId) {
        localMsgDataSource.expiredMsg(userId, robotId, commandId);
    }

    public void saveAll(List<Message> messages) {
        localMsgDataSource.save(messages);
    }

    public void clearAll(int type) {
        localMsgDataSource.clearAll(type);
    }

    public void readMessage(final String noticeId) {
        remoteMsgDataSource.readMsg(noticeId, new IMsgDataSource.ReadMsgCallback() {
            @Override
            public void onSuccess() {
                localMsgDataSource.readMsg(noticeId, null);
            }

            @Override
            public void onError() {

            }
        });
    }

    public void readAllMessage(final String userId, final int type, final IMsgDataSource.ReadMsgCallback callback) {
        remoteMsgDataSource.readAllMsg(userId, type, new IMsgDataSource.ReadMsgCallback() {
            @Override
            public void onSuccess() {
                localMsgDataSource.readAllMsg(userId, type, new IMsgDataSource.ReadMsgCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError() {
                if(callback != null){
                    callback.onError();
                }
            }
        });
    }

}
