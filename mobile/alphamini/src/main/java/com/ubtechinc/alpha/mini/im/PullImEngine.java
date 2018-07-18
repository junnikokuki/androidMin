package com.ubtechinc.alpha.mini.im;

import android.support.annotation.NonNull;

import com.ubtrobot.common.config.LightSettings;
import com.ubtrobot.common.utils.XLog;
import com.ubtrobot.lib.communation.mobile.connection.message.Param;
import com.ubtrobot.lib.communation.mobile.connection.transport.ProtoParam;
import com.ubtrobot.lib.sync.engine.IPageLoader;
import com.ubtrobot.lib.sync.engine.QueryPacket;
import com.ubtrobot.lib.sync.engine.ResultPacket;
import com.ubtrobot.lib.sync.engine.socket.utils.PullSocketUtils;
import com.ubtrobot.lib.sync.utils.SeqIdUtils;
import com.ubtrobot.param.sync.Params;
import com.ubtrobot.param.sync.Paths;

/**
 * Created by Mark.Zhang on 2017/10/30.
 */

public class PullImEngine extends ImEngine<Params.PullRequest, Params.PullResponse> implements IPageLoader {
    public String mContext;
    public boolean mHasNext = false;
    private int mPageSize = IPageLoader.PAGE_SIZE;

    public PullImEngine() {
        super(Paths.HOST, Paths.Request.PULL_META_DATA);
    }

    @Override
    public long start() {
        long seqId = SeqIdUtils.next();
        Param request = createRequest(seqId, null, mPageSize);
        send(request);
        return seqId;
    }

    @Override
    public boolean hasNext() {
        return mHasNext;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.mPageSize = pageSize;
    }

    @Override
    public int getPageSize() {
        return mPageSize;
    }

    @Override
    public String context() {
        if (mContext != null) {
            return mContext;
        }
        return "";
    }

    @Override
    public long next() {
        if (hasNext()) {
            long seqId = SeqIdUtils.next();
            Param request = createRequest(seqId, mContext, mPageSize);
            send(request);
            return seqId;
        }
        return -1L;
    }

    private Param createRequest(long seqId, String context, int pageSize) {
        Params.PullRequest.Builder local = Params.PullRequest.newBuilder();
        //PacketHub packetHub = PacketHub.instance();
        if (context != null) {
            local.setContext(context);
        }
        local.setSeqId(seqId);
        String lastServerUuid = LightSettings.get().getString(LightSettings.KEY_LAST_ROBOT_UUID);
        if (lastServerUuid != null) {
            local.setLastServerUuid(lastServerUuid);
        }
        local.setPageSize(pageSize);
        //local.setClientDataVersion(LightSettings.get().getLong(LightSettings.KEY_ROBOT_DB_VERSION, -1L));
        local.setClientDataVersion(-1L);
        return ProtoParam.pack(local.build());
    }

    @Override
    protected void onSuccess(@NonNull Param request, @NonNull Param response) {
        XLog.d("sync", " onresponse %s", response);

        // cancel last time request-response
        QueryPacket<Params.PullRequest> query = new QueryPacket<>();

        try {
            query.request = ProtoParam.from(request).unpack(Params.PullRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (query.request == null) {
            // not the same seq, just return
            //XLog.d("sync", "cancel seq %s, cur seq %s", syncReq != null ? syncReq.getSeqId() : -1, mSeqId);
            mUICallback.onFail(query.id,query, -1, "request is null");
            return;
        }

        ResultPacket<Params.PullResponse> result = new ResultPacket<>();
        try {
            result.response = ProtoParam.from(response).unpack(Params.PullResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.response == null) {
            //
            XLog.d("sync", "svrData is null");
            mUICallback.onFail(query.id,query, -1, "response is null");
            return;
        }

        if (result.response.getContext() != null) {
            mContext = result.response.getContext();
        }
        else {
            mContext = null;
        }

        // set next
        mHasNext = result.response.getHasNext();

        XLog.d("sync", "server uuid %s", result.response.getServerUuid()+"mHasNext"+mHasNext);
        // 0.14KB per item
        //XLog.d("sync", "serialized size %s, byte len %s", syncResp.getSerializedSize(), syncResp.toByteArray().length);
        LightSettings.get().setString(LightSettings.KEY_LAST_ROBOT_UUID, result.response.getServerUuid());
        //result.setObj(Result.Obj.OBJ_ROBOT);
        if (query.request.getContext() == null
                || query.request.getContext().isEmpty()) {
            PullSocketUtils.replace(result.response);
        }
        else {
            PullSocketUtils.merge(result.response);
        }

        // update version record
        long localVer = LightSettings.get().getLong(LightSettings.KEY_ROBOT_DB_VERSION, -1L);
        long svrVer = result.response.getServerDataVersion();
        XLog.d("sync", "localVer:%s, srvVer:%s", localVer, svrVer);
        if (svrVer > localVer) {
            LightSettings.get().setLong(LightSettings.KEY_ROBOT_DB_VERSION, svrVer);
        }

        mUICallback.onSuccess(query.id,query, result);
    }

    @Override
    protected void onFail(@NonNull Param request, int code, String message) {
        XLog.d("sync", " request %s", request);

        // cancel last time request-response
        QueryPacket<Params.PullRequest> query = new QueryPacket<>();

        try {
            query.request = ProtoParam.from(request).unpack(Params.PullRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (query.request == null) {
            // not the same seq, just return
            //XLog.d("sync", "cancel seq %s, cur seq %s", syncReq != null ? syncReq.getSeqId() : -1, mSeqId);
            mUICallback.onFail(query.id,query, -1, "request is null");
            return;
        }

        XLog.d("sync", " not cancel");
        mUICallback.onFail(query.id ,query, code, message);
    }

}
