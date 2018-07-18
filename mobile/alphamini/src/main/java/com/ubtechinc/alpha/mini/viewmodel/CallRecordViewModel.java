package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.Page;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.repository.CallRecordRepository;
import com.ubtechinc.alpha.mini.repository.datasource.ICallRecordDataSource;

import java.util.List;

public class CallRecordViewModel {

    public static final String TAG = "CallRecordViewModel";
    public static final String VERSION_CODE_KEY = "call_record_version";

    private CallRecordRepository callRecordRepository;

    public CallRecordViewModel() {
        callRecordRepository = new CallRecordRepository();
    }

    public LiveResult<List<CallRecord>> sync(final String robotId) {
        long versionCode = SPUtils.get().getLong(VERSION_CODE_KEY + robotId, -1l);
        final LiveResult<List<CallRecord>> result = new LiveResult<>();
        Page page = new Page();
        page.setCurrentPage(1);
        page.setRobotId(robotId);
        page.setVersionCode(versionCode);
        page.setFrom(0);
        syncFromRobot(result, robotId, page);
        return result;
    }

    private void syncFromRobot(final LiveResult<List<CallRecord>> result, final String robotId, final Page page) {
        callRecordRepository.getCallRecord(page, new ICallRecordDataSource.GetCallRecordsCallback() {
            @Override
            public void onSuccess(Page<CallRecord> page) {
                if (page.getCurrentPage() >= page.getTotalPage()) {
                    SPUtils.get().put(VERSION_CODE_KEY + robotId, page.getVersionCode());
                    result.postSuccess(page.getData());
                } else {
                    result.loading(page.getData());
                    page.setCurrentPage(page.getCurrentPage() + 1);
                    syncFromRobot(result, robotId, page);
                }
            }

            @Override
            public void onError(int code, String msg) {
                syncFromRobot(result, robotId, page);
            }
        });
    }

    public LiveResult<List<CallRecord>> load(int from, String robotId) {
        final LiveResult<List<CallRecord>> result = new LiveResult<>();
        Page page = new Page();
        page.setFrom(from);
        page.setRobotId(robotId);
        callRecordRepository.getCallRecordFromDB(page, new ICallRecordDataSource.GetCallRecordsCallback() {
            @Override
            public void onSuccess(Page<CallRecord> page) {
                result.success(page.getData());
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code,msg);
            }
        });
        return result;
    }

}
