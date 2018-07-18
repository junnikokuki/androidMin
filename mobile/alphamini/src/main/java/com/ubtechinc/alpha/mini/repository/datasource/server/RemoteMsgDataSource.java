package com.ubtechinc.alpha.mini.repository.datasource.server;


import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.repository.datasource.IMsgDataSource;
import com.ubtechinc.alpha.mini.net.GetMsgModule;
import com.ubtechinc.alpha.mini.net.GetNoReadMsgModule;
import com.ubtechinc.alpha.mini.net.ReadAllMessageModule;
import com.ubtechinc.alpha.mini.net.ReadMessageModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.List;

public class RemoteMsgDataSource implements IMsgDataSource {

    public static final String TAG = "RemoteMsgDataSource";

    @Override
    public void getMsgs(String userId, int type, int page, int pagesize, final GetMsgsCallback callback) {
        Log.d(TAG, "getMsgs");
        GetMsgModule.Request request = new GetMsgModule().new Request();
        request.setPage(page);
        request.setPagesize(pagesize);
        request.setNoticeType(type);
        HttpProxy.get().doGet(request, new ResponseListener<GetMsgModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onError();
                }
            }

            @Override
            public void onSuccess(GetMsgModule.Response response) {
                if (response.isSuccess() && response.getData() != null) {
                    if (callback != null) {
                        List<Message> sysMsgs = response.getData().getSystemResult();
                        List<Message> shareMsgs = response.getData().getShareResult();
                        Log.d(TAG, "getMsgs result sys= " + (CollectionUtils.isEmpty(sysMsgs) ? 0 : sysMsgs.size()));
                        Log.d(TAG, "getMsgs result shareMsgs= " + (CollectionUtils.isEmpty(shareMsgs) ? 0 : shareMsgs.size()));
                        callback.onSuccess(response.getData().getSystemResult(), response.getData().getShareResult());
                    }
                } else {
                    if (callback != null) {
                        callback.onError();
                    }
                }
            }
        });

    }

    @Override
    public void getMsg(String noticeId, GetMsgCallback callback) {

    }

    @Override
    public void getNoReadCount(final GetNoReadCountCallback callback) {
        final GetNoReadMsgModule.Request request = new GetNoReadMsgModule().new Request();
        HttpProxy.get().doGet(request, new ResponseListener<GetNoReadMsgModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                if (callback != null) {
                    callback.onError();
                }
            }

            @Override
            public void onSuccess(GetNoReadMsgModule.Response response) {
                if (response.isSuccess() && response.getData() != null) {
                    if (callback != null) {
                        callback.onSuccess(response.getData().getSystemMsgCount(), response.getData().getShareMsgCount());
                    }
                } else {
                    if (callback != null) {
                        callback.onError();
                    }
                }
            }
        });
    }

    @Override
    public void readMsg(String noticeId, final ReadMsgCallback callback) {
        ReadMessageModule.Request request = new ReadMessageModule().new Request();
        request.setNoticeIds(noticeId);
        HttpProxy.get().doPost(request, new ResponseListener<ReadMessageModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError();
            }

            @Override
            public void onSuccess(ReadMessageModule.Response response) {
                if (response.isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onError();
                }
            }
        });
    }

    @Override
    public void readAllMsg(String userId, int type, final ReadMsgCallback callback) {
        ReadAllMessageModule.Request request = new ReadAllMessageModule().new Request();
        request.setNoticeType(type);
        HttpProxy.get().doPost(request, new ResponseListener<ReadAllMessageModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError();
            }

            @Override
            public void onSuccess(ReadAllMessageModule.Response response) {
                if (response.isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onError();
                }
            }
        });
    }

}
