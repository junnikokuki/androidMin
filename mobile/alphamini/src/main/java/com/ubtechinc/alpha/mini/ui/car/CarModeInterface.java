package com.ubtechinc.alpha.mini.ui.car;

import com.ubtechinc.alpha.JimuCarGetBleList;
import com.ubtechinc.alpha.JimuCarPower;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * Created by riley.zhang on 2018/7/12.
 */

public class CarModeInterface {

    public interface ICarPowerListener{
        void onSuccess(JimuCarPower.GetJimuCarPowerResponse response);
        void onFail(ThrowableWrapper e);
    }

    public interface ICarListListener{
        void onSuccess(JimuCarGetBleList.GetJimuCarBleListResponse response);
        void onFail(ThrowableWrapper e);
    }

    public interface ICarConnectedListener{
        void onSuccess();
        void onFail();
    }
}
