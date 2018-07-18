package com.ubtechinc.alpha.mini.repository.datasource.db;

import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.repository.datasource.ICallRecordDataSource;
import com.ubtechinc.alpha.mini.database.CallRecordProvider;

import java.util.List;

/**
 * 从数据库操作通话记录数据源
 * Created by ubt on 2018/2/3.
 */

public class DbCallRecordDataSource implements ICallRecordDataSource{

    private int pageSize = 15;

    @Override
    public void getCallRecords(Page page, GetCallRecordsCallback callback) {
        List<CallRecord> callRecords = CallRecordProvider.get().getByRobotId(page.getRobotId(),page.getFrom(),pageSize);
        page.setData(callRecords);
        if (callback != null) {
            callback.onSuccess(page);
        }
    }

    @Override
    public void saveCallRecords(List<CallRecord> callRecords, SaveCallRecordsCallback callback) {
        CallRecordProvider.get().saveOrUpdateAll(callRecords);
        if(callback != null){
            callback.onSuccess();
        }
    }
}
