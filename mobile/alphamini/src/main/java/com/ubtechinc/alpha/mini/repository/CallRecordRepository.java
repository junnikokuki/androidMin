package com.ubtechinc.alpha.mini.repository;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.ICallRecordDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.db.DbCallRecordDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.im.IMCallRecordDataSource;

import java.util.List;


public class CallRecordRepository {

    private DbCallRecordDataSource dbCallRecordDataSource;
    private IMCallRecordDataSource imCallRecordDataSource;

    public CallRecordRepository() {
        imCallRecordDataSource = new IMCallRecordDataSource();
        dbCallRecordDataSource = new DbCallRecordDataSource();
    }

    public void getCallRecord(Page<CallRecord> page, final ICallRecordDataSource.GetCallRecordsCallback callback) {
        imCallRecordDataSource.getCallRecords(page, new ICallRecordDataSource.GetCallRecordsCallback() {
            @Override
            public void onSuccess(Page<CallRecord> page) {
                if (!CollectionUtils.isEmpty(page.getData())) {
                    saveCallRecordRepository(page.getData(), null);
                }
                if (callback != null) {
                    callback.onSuccess(page);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null) {
                    callback.onError(code, msg);
                }
            }
        });
    }

    public void getCallRecordFromDB(Page<CallRecord> page, ICallRecordDataSource.GetCallRecordsCallback callback) {
        dbCallRecordDataSource.getCallRecords(page, callback);
    }

    public void saveCallRecordRepository(List<CallRecord> records, ICallRecordDataSource.SaveCallRecordsCallback callback) {
        dbCallRecordDataSource.saveCallRecords(records, callback);
    }
}
