package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.adapter.AvatarActionHAdapter;
import com.ubtechinc.alpha.mini.avatar.db.AvatarActionHelper;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/18 14:59
 * @描述: 横屏幕动作列表
 */

public class ActionPanelHUtil {

    private Activity mActivity;

    private RecyclerView rvAllAction;

    private AvatarControlListener avatarControlListener;

    private AvatarActionHAdapter mAdapter;

    private ControlPanelHUtil util;

    public ActionPanelHUtil(Activity activity, AvatarControlListener avatarControlListener, ControlPanelHUtil util) {
        mActivity = activity;
        this.avatarControlListener = avatarControlListener;
        this.util = util;
        init();
    }

    private void init() {
        List<AvatarActionModel> avatarActionModels = AvatarActionHelper.getInstance().getAvatarActionModelList();
        avatarActionModels.addAll(AvatarActionHelper.getInstance().getAllAvatarActionModelList());
        mAdapter = new AvatarActionHAdapter(mActivity, avatarActionModels, avatarControlListener);
        mAdapter.setControlPanelHUtil(util);
        rvAllAction = mActivity.findViewById(R.id.rv_all_action);
        rvAllAction.setLayoutManager(new LinearLayoutManager(mActivity));
        rvAllAction.setAdapter(mAdapter);
    }


    public void disableActions(){
        mAdapter.disableAction();
    }

    public void enableActions(){
        mAdapter.enableActions();
    }




}
