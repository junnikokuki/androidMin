package com.ubtechinc.alpha.mini.avatar.adapter;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.viewutils.ActionPanelUtil;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;
import com.ubtechinc.alpha.mini.avatar.widget.RoundProgressImageView;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/16 16:23
 * @描述:
 */

public class AvatarActionAdapter extends RecyclerView.Adapter<AvatarActionAdapter.ActionHolder> implements RoundProgressImageView.AnimationListener {

    private String TAG = getClass().getSimpleName();

    private Activity mCtx;

    private List<AvatarActionModel> actionModels;

    private boolean doingAction = false;

    private int doActionIndex = -1;

    private AvatarControlListener avatarControlListener;

    private ActionPanelUtil utils;

    private ActionPanelUtil.IActionExcuteListener listener;

    public AvatarActionAdapter(Activity ctx, List<AvatarActionModel> actionMode, ActionPanelUtil utils) {
        this.mCtx = ctx;
        this.utils = utils;
        this.actionModels = actionMode;
    }

    public void setActionExcuteListener(ActionPanelUtil.IActionExcuteListener listener){
        this.listener = listener;
    }

    public void setAvatarControlListener(AvatarControlListener avatarControlListener) {
        this.avatarControlListener = avatarControlListener;
    }


    @Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_avatar_action,  null);
        return new ActionHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActionHolder holder, final int position) {
        AvatarActionModel model = actionModels.get(position);
        holder.mTextView.setText(model.getActionNameCN());
        Drawable drawable = mCtx.getResources().getDrawable(actionModels.get(position).getShadowImageIconId());
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.mTextView.setCompoundDrawables(null, drawable, null, null);
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarControlListener.onAction(actionModels.get(position).getActionNameEN());
                handleItemClick(position, holder.mIvProgress);
            }
        });
        handleItemState(holder, position);
    }

    private void handleItemState(ActionHolder holder, int position) {
        if (!doingAction){
            holder.mRoot.setAlpha(1f);
            holder.mRoot.setClickable(true);
            return;
        }

        if (position == doActionIndex){
            holder.mRoot.setAlpha(1f);
        }else{
            holder.mRoot.setAlpha(0.3f);
        }

        holder.mRoot.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return actionModels.size();
    }

    /**
     * 点击事件,所有动作的动画
     *
     * @param position
     */
    private void handleItemClick(final int position, RoundProgressImageView view) {
        doActionIndex = position;
        utils.disableHeaderAction();
        doingAction = true;
        view.startAnimation();
        view.setAlpha(1f);
        if (listener != null){
            listener.onActionStart();
        }
        notifyDataSetChanged();
    }

    public void enableHideAction(){
        doingAction = false;
        doActionIndex = -1;
        notifyDataSetChanged();
    }

    public void disableHideAction(){
        doingAction = true;
        doActionIndex = -1;
        notifyDataSetChanged();
    }


    @Override
    public void onAnmiationEnd() {
        utils.enableHeaderAction();
        enableHideAction();
        if (listener != null){
            listener.onActionStop();
        }
    }


    public class ActionHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public RoundProgressImageView mIvProgress;

        public View mRoot;

        public ActionHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mTextView = itemView.findViewById(R.id.tv_action);
            mIvProgress = itemView.findViewById(R.id.iv_progress);
            mIvProgress.setAnmiationListener(AvatarActionAdapter.this);
        }
    }

}
