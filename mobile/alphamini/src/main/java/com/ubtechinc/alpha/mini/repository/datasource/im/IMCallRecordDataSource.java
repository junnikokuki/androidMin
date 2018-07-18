package com.ubtechinc.alpha.mini.repository.datasource.im;

import android.util.Log;

import com.ubtechinc.alpha.CmQueryCallRecord;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.ICallRecordDataSource;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubt on 2018/2/3.
 */

public class IMCallRecordDataSource implements ICallRecordDataSource {

    public static final String TAG = "IMCallRecordDataSource";

    @Override
    public void getCallRecords(final Page page, final GetCallRecordsCallback callback) {
        CmQueryCallRecord.CmQueryCallRecordRequest.Builder builder = CmQueryCallRecord.CmQueryCallRecordRequest.newBuilder();
        builder.setCurrentPage(page.getCurrentPage());
        builder.setVersionCode(page.getVersionCode());
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_CALLRECORD_REQUEST, IMCmdId.IM_VERSION, builder.build(), page.getRobotId(), new ICallback<CmQueryCallRecord.CmQueryCallRecordResponse>() {
            @Override
            public void onSuccess(CmQueryCallRecord.CmQueryCallRecordResponse data) {
                Log.d(TAG,"getCallRecords success = " +data.toString());
                List<CallRecord> callRecords = new ArrayList<CallRecord>();
                for (CmQueryCallRecord.CmCallRecordInfo cmCallRecordInfo : data.getContactListList()) {
                    CallRecord callRecord = new CallRecord();
                    callRecord.setRobotId(page.getRobotId());
                    callRecord.setType(cmCallRecordInfo.getType());
                    callRecord.setCallTime(Long.valueOf(cmCallRecordInfo.getCallTime() * 1000));
                    callRecord.setCallId(cmCallRecordInfo.getCallId());
                    callRecord.setDuration(cmCallRecordInfo.getDuration());
                    callRecord.setCallerName(cmCallRecordInfo.getCallerName());
                    callRecords.add(callRecord);
                }
                page.setData(callRecords);
                page.setTotalPage(data.getTotalPage());
                if (page.getCurrentPage() >= data.getTotalPage()) {
                    page.setVersionCode(data.getVersionCode());
                }
                if (callback != null) {
                    callback.onSuccess(page);
                }
            }

            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG,"getCallRecords onError = " +e.toString());
                if (callback != null) {
                    callback.onError(e.getErrorCode(), e.getMessage());
                }
            }
        });

    }

    @Override
    public void saveCallRecords(List<CallRecord> callRecords, SaveCallRecordsCallback callback) {
        //do nothing
    }

}
