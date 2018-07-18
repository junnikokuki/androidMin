package com.ubtechinc.alpha.mini.entity;

import android.arch.lifecycle.LiveData;

import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :  代表动作相关数据
 */

public class RepresentActionLive extends LiveData<RepresentActionLive> {

    private String mRepresentiveAction;

    private List<ActionRepresentiveRecomandListModule.RecomandAction> recomandActions = new ArrayList<>();

    public String getAction() {
        return mRepresentiveAction;
    }

    public void setRepresentiveAction(String mRepresentiveAction) {
        this.mRepresentiveAction = mRepresentiveAction;
        setValue(this);
    }

    public void setRepresentListAction(List<ActionRepresentiveRecomandListModule.RecomandAction> actions){
        recomandActions.clear();
        recomandActions.addAll(actions);
        setValue(this);
    }

    public List<ActionRepresentiveRecomandListModule.RecomandAction> getRecomandActions() {
        return recomandActions;
    }
}
