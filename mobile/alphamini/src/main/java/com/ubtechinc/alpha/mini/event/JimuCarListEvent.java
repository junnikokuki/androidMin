package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.alpha.JimuCarGetBleList;
import com.ubtechinc.alpha.mini.entity.CarListInfo;
import java.util.ArrayList;

/**
 * Created by riley.zhang on 2018/7/12.
 */

public class JimuCarListEvent {

    ArrayList<CarListInfo> mCarList = new ArrayList<>();
    public JimuCarListEvent(ArrayList<CarListInfo> carList) {
        mCarList = carList;
    }

    public ArrayList<CarListInfo> getCarList() {
        return mCarList;
    }
}
