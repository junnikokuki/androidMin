package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.Page;

import java.util.List;

/**
 * Created by ubt on 2018/2/3.
 */

public interface ICallRecordDataSource {

    public void getCallRecords(Page page, GetCallRecordsCallback callback);

    public void saveCallRecords(List<CallRecord> callRecords,SaveCallRecordsCallback callback);

    public interface GetCallRecordsCallback {

        public void onSuccess(Page<CallRecord> page);

        public void onError(int code, String msg);
    }

    public interface SaveCallRecordsCallback{

        public void onSuccess();

        public void onError(int code, String msg);
    }
}
