package com.ubtechinc.alpha.mini.im;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ubtrobot.lib.communation.mobile.connection.message.Param;
import com.ubtrobot.lib.communation.mobile.connection.transport.ProtoParam;
import com.ubtrobot.lib.sync.engine.QueryPacket;
import com.ubtrobot.lib.sync.engine.ResultPacket;
import com.ubtrobot.lib.sync.utils.SeqIdUtils;
import com.ubtrobot.param.sync.Files;
import com.ubtrobot.param.sync.Params;
import com.ubtrobot.param.sync.Paths;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class PushImEngine extends ImEngine<Params.PushRequest, Params.PushResponse> {
    public List<Files.ModifyData> modifyModels ;
    public PushImEngine() {
        super(Paths.HOST, Paths.Request.PUSH_META_DATA);
    }
    @Override
    public long start() {
        long seqId = SeqIdUtils.next();
        Param request = createRequest(seqId,modifyModels);
        send(request);
        return seqId;
    }

    private Param createRequest(long seqId,List<Files.ModifyData> modifyModels) {
        Params.PushRequest.Builder local = Params.PushRequest.newBuilder();
        local.setSeqId(seqId);
        local.addAllOperate(modifyModels);
        Log.d("0423","modifyModels  ....modifyModels"+modifyModels.size()+modifyModels.get(0).getAction()+modifyModels.size()+modifyModels.get(0).getPath());

        return ProtoParam.pack(local.build());
    }

    @Override
    protected void onSuccess(@NonNull Param request, @NonNull Param response) {
        QueryPacket<Params.PushRequest> query = new QueryPacket<>();
        try {
            query.request = ProtoParam.from(request).unpack(Params.PushRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (query.request == null) {
            // not the same seq, just return
            //XLog.d("sync", "cancel seq %s, cur seq %s", syncReq != null ? syncReq.getSeqId() : -1, mSeqId);

            mUICallback.onFail(query.id,query, -1, "request is null");
            return;
        }
        ResultPacket<Params.PushResponse> result = new ResultPacket<>();
        try {
            result.response = ProtoParam.from(response).unpack(Params.PushResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result.response == null || result.response.getCode() != 0) {
            mUICallback.onFail(query.id,query, -1, "response is null or code not zero");
            return;
        }

        List<Files.ModifyData> resultList = query.request.getOperateList();
        PushImUtils.dealResult(resultList);
        mUICallback.onSuccess(query.id,query, result);
    }

    @Override
    protected void onFail(@NonNull Param request, int code, String message) {
        QueryPacket<Params.PushRequest> query = new QueryPacket<>();
        try {
            query.request = ProtoParam.from(request).unpack(Params.PushRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUICallback.onFail(query.id,query, -1, message);
    }
}
