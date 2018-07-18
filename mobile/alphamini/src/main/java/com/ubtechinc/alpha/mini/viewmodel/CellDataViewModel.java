package com.ubtechinc.alpha.mini.viewmodel;

import com.ubtechinc.alpha.mini.entity.SimState;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.repository.CellularDataRepository;

/**
 * Created by ubt on 2018/2/6.
 */

public class CellDataViewModel {

    private CellularDataRepository cellularDataRepository;

    public CellDataViewModel() {
        cellularDataRepository = new CellularDataRepository();
    }

    public LiveResult<SimState> getCellDataState() {
        final LiveResult<SimState> simStateLiveResult = new LiveResult<>();
        cellularDataRepository.getState(MyRobotsLive.getInstance().getRobotUserId(), new CellularDataRepository.GetStateCallback() {
            @Override
            public void onSuccess(boolean isCellOpened, boolean isRoamingOpened) {
                SimState simState = new SimState();
                simState.setCellData(isCellOpened);
                simState.setRoamData(isRoamingOpened);
                simStateLiveResult.success(simState);
            }

            @Override
            public void onError(int code, String msg) {
                simStateLiveResult.fail(code, msg);
            }
        });
        return simStateLiveResult;
    }

    public LiveResult opCellData(boolean isOpen) {
        final LiveResult liveResult = new LiveResult();
        cellularDataRepository.switchDataStatus(MyRobotsLive.getInstance().getRobotUserId(), isOpen, new CellularDataRepository.StateOpCallback() {
            @Override
            public void onSuccess() {
                liveResult.success(null);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }


    public LiveResult opRoamData(boolean isOpen) {
        final LiveResult liveResult = new LiveResult();
        cellularDataRepository.switchRoamStatus(MyRobotsLive.getInstance().getRobotUserId(), isOpen, new CellularDataRepository.StateOpCallback() {
            @Override
            public void onSuccess() {
                liveResult.success(null);
            }

            @Override
            public void onError(int code, String msg) {
                liveResult.fail(code, msg);
            }
        });
        return liveResult;
    }
}
