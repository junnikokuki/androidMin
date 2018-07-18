package com.ubtechinc.alpha.mini.repository.datasource;


import com.ubtechinc.alpha.mini.entity.Message;

import java.util.List;

public interface IMsgDataSource {

    public void getMsgs(String userId, int type, int page, int pagesize, GetMsgsCallback callback);

    public void getMsg(String noticeId, GetMsgCallback callback);

    public void getNoReadCount(GetNoReadCountCallback callback);

    public void readMsg(String noticeId,ReadMsgCallback callback);

    public void readAllMsg(String userId,int type,ReadMsgCallback callback);

    public interface GetNoReadCountCallback {
        public void onSuccess(int systemCount, int shareCount);

        public void onError();
    }

    public interface GetMsgsCallback {

        public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs);

        public void onError();
    }

    public interface GetMsgCallback {
        public void onSuccess(Message message);

        public void onError();
    }

    public interface ReadMsgCallback {
        public void onSuccess();

        public void onError();
    }
}
