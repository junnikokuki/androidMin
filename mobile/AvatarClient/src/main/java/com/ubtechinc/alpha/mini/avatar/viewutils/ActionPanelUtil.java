package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.adapter.AvatarActionAdapter;
import com.ubtechinc.alpha.mini.avatar.db.AvatarActionHelper;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;
import com.ubtechinc.alpha.mini.avatar.widget.RoundProgressImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/17 17:22
 * @描述: 动作列表操作的文件
 */

public class ActionPanelUtil implements View.OnClickListener,RoundProgressImageView.AnimationListener {

    private String TAG = getClass().getSimpleName();

    private Activity activity;

    private RecyclerView rvAction;
    private View vShowUpAction;
    private View vDismisAction;
    private View spliteLine;

    private TextView tvHeaderActionOne;
    private TextView tvHeaderActionTwo;
    private TextView tvHeaderActionThree;
    private TextView tvHeaderActionFour;

    private List<RoundProgressImageView> progressImageViews = new ArrayList<>();
    private List<RelativeLayout> mActionLayouts = new ArrayList<>();

    private List<AvatarActionModel> favActionList;

    private AvatarControlListener listener;

    private AvatarActionAdapter mAdapter;

    private IActionExcuteListener actionExcuteListener;

    public ActionPanelUtil(Activity activity, List<AvatarActionModel> favActionList, AvatarControlListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.favActionList = favActionList;
        init();
    }

    public void setActionListener(IActionExcuteListener actionExcuteListener){
        this.actionExcuteListener = actionExcuteListener;
        if (mAdapter != null){
            mAdapter.setActionExcuteListener(actionExcuteListener);
        }
    }

    private void init() {
        vShowUpAction = activity.findViewById(R.id.iv_show_action);
        vDismisAction = activity.findViewById(R.id.iv_dissmiss_action);
        spliteLine    = activity.findViewById(R.id.split_line);
        tvHeaderActionOne   = activity.findViewById(R.id.header_row_one);
        tvHeaderActionTwo   = activity.findViewById(R.id.header_row_two);
        tvHeaderActionThree = activity.findViewById(R.id.header_row_three);
        tvHeaderActionFour  = activity.findViewById(R.id.header_row_four);

        RoundProgressImageView imageViewP1 = activity.findViewById(R.id.iv_progress_one);
        RoundProgressImageView imageViewP2 = activity.findViewById(R.id.iv_progress_two);
        RoundProgressImageView imageViewP3 = activity.findViewById(R.id.iv_progress_three);
        RoundProgressImageView imageViewP4 = activity.findViewById(R.id.iv_progress_four);
        progressImageViews.add(imageViewP1);
        progressImageViews.add(imageViewP2);
        progressImageViews.add(imageViewP3);
        progressImageViews.add(imageViewP4);

        RelativeLayout rlOne = activity.findViewById(R.id.rl_action_one);
        RelativeLayout rlTwo = activity.findViewById(R.id.rl_action_two);
        RelativeLayout rlThree = activity.findViewById(R.id.rl_action_three);
        RelativeLayout rlFour = activity.findViewById(R.id.rl_action_four);
        mActionLayouts.add(rlOne);
        mActionLayouts.add(rlTwo);
        mActionLayouts.add(rlThree);
        mActionLayouts.add(rlFour);

        rvAction = activity.findViewById(R.id.rv_action);

        setHeaderView();
        setRecyleViewAction();
        initListener();
    }

    private void setRecyleViewAction() {
        mAdapter = new AvatarActionAdapter(activity, AvatarActionHelper.getInstance().getAllAvatarActionModelList(), this);
        mAdapter.setAvatarControlListener(listener);
        rvAction.setAdapter(mAdapter);
        rvAction.setLayoutManager(new GridLayoutManager(activity, 4));
    }



    private void initListener() {
        vShowUpAction.setOnClickListener(this);
        vDismisAction.setOnClickListener(this);
        for (RelativeLayout relativeLayout : mActionLayouts){
            relativeLayout.setOnClickListener(this);
        }
        for (RoundProgressImageView imageView : progressImageViews){
            imageView.setAnmiationListener(this);
        }
    }



    private void setHeaderView() {
        if (favActionList == null || favActionList.size() == 0) {
            Log.e(TAG, "favActionList list is null!!");
            return;
        }
        Drawable drawable1 = activity.getResources().getDrawable(favActionList.get(0).getShadowImageIconId());
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        tvHeaderActionOne.setText(favActionList.get(0).getActionNameCN());
        tvHeaderActionOne.setCompoundDrawables(null, drawable1, null, null);

        Drawable drawable2 = activity.getResources().getDrawable(favActionList.get(1).getShadowImageIconId());
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        tvHeaderActionTwo.setText(favActionList.get(1).getActionNameCN());
        tvHeaderActionTwo.setCompoundDrawables(null, drawable2, null, null);

        Drawable drawable3 = activity.getResources().getDrawable(favActionList.get(2).getShadowImageIconId());
        drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
        tvHeaderActionThree.setText(favActionList.get(2).getActionNameCN());
        tvHeaderActionThree.setCompoundDrawables(null, drawable3, null, null);

        Drawable drawable4 = activity.getResources().getDrawable(favActionList.get(3).getShadowImageIconId());
        drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
        tvHeaderActionFour.setText(favActionList.get(3).getActionNameCN());
        tvHeaderActionFour.setCompoundDrawables(null, drawable4, null, null);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_show_action) {
            showUpAllAction();
        } else if (v.getId() == R.id.iv_dissmiss_action) {
            dismisAllAction();
        } else if (v.getId() == R.id.rl_action_one){
            listener.onAction(favActionList.get(0).getActionNameEN());
            handlClickItem(progressImageViews.get(0), 0);
        } else if (v.getId() == R.id.rl_action_two){
            listener.onAction(favActionList.get(1).getActionNameEN());
            handlClickItem(progressImageViews.get(1), 1);
        } else if (v.getId() == R.id.rl_action_three){
            listener.onAction(favActionList.get(2).getActionNameEN());
            handlClickItem(progressImageViews.get(2), 2);
        } else if (v.getId() == R.id.rl_action_four){
            listener.onAction(favActionList.get(3).getActionNameEN());
            handlClickItem(progressImageViews.get(3), 3);
        }
    }

    public void handlClickItem(RoundProgressImageView imageView, int position){
        if (actionExcuteListener != null){
            actionExcuteListener.onActionStart();
        }
        disableAllAction();
        imageView.startAnimation();
        mActionLayouts.get(position).setAlpha(1f);
    }

    public void disableAllAction(){
        disableHeaderAction();
        mAdapter.disableHideAction();
    }

    public void enableAllAction(){
        enableHeaderAction();
        mAdapter.enableHideAction();
    }

    public void disableHeaderAction() {
        for (RelativeLayout layout : mActionLayouts){
            layout.setClickable(false);
            layout.setAlpha(0.3f);
        }
    }

    public void enableHeaderAction(){
        for (RelativeLayout layout : mActionLayouts){
            layout.setClickable(true);
            layout.setAlpha(1);
        }
    }

    @Override
    public void onAnmiationEnd() {
        enableAllAction();
        if (actionExcuteListener != null){
            actionExcuteListener.onActionStop();
        }
    }


    private void dismisAllAction() {
        rvAction.setVisibility(View.GONE);
        vDismisAction.setVisibility(View.GONE);
        spliteLine.setVisibility(View.GONE);
        vShowUpAction.setVisibility(View.VISIBLE);
    }

    private void showUpAllAction() {
        rvAction.setVisibility(View.VISIBLE);
        vDismisAction.setVisibility(View.VISIBLE);
        spliteLine.setVisibility(View.VISIBLE);
        vShowUpAction.setVisibility(View.GONE);
    }


    public interface IActionExcuteListener{
        void onActionStart();

        void onActionStop();
    }
}
