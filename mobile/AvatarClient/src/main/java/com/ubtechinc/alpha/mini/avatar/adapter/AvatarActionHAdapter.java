package com.ubtechinc.alpha.mini.avatar.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.viewutils.ControlPanelHUtil;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;
import com.ubtechinc.alpha.mini.avatar.widget.RoundProgressImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/5/18 15:10
 * @描述: 横屏幕的动作列表
 */

public class AvatarActionHAdapter extends RecyclerView.Adapter<AvatarActionHAdapter.AvatarActionViewHolder> implements RoundProgressImageView.AnimationListener {

    private List<AvatarActionModel> allActions = new ArrayList<>();

    private Activity mActivity;

    private AvatarControlListener avatarControlListener;

    private boolean isDoingAction;

    private int currentIndexOfDoing = -1;

    private ControlPanelHUtil util;


    public AvatarActionHAdapter(Activity activity, List<AvatarActionModel> actions, AvatarControlListener avatarControlListener) {
        mActivity = activity;
        this.avatarControlListener = avatarControlListener;
        allActions.addAll(actions);
    }

    public void setControlPanelHUtil(ControlPanelHUtil util){
        this.util = util;
    }

    @Override
    public AvatarActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.action_item_h, null);
        return new AvatarActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AvatarActionViewHolder holder, final int position) {
        AvatarActionModel model = allActions.get(position);
        holder.mTextView.setText(model.getActionNameCN());
        Drawable drawable = mActivity.getResources().getDrawable(allActions.get(position).getShadowImageIconId());
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.mTextView.setCompoundDrawables(null, drawable, null, null);
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 18/5/29 第一次没有动画，为什么会没有动画呢？
                avatarControlListener.onAction(allActions.get(position).getActionNameEN());
                handleClickItem(holder, position);
            }
        });
        handleItemState(holder, position);
    }

    private void handleItemState(AvatarActionViewHolder holder, int position) {
        if (!isDoingAction){
            holder.mRoot.setClickable(true);
            holder.mRoot.setAlpha(1f);
            return;
        }

        if (currentIndexOfDoing == position){
            holder.mRoot.setAlpha(1f);
        }else{
            holder.mRoot.setAlpha(0.3f);
        }
        holder.mRoot.setClickable(false);
    }

    private void handleClickItem(AvatarActionViewHolder holder, int position) {
        isDoingAction = true;
        currentIndexOfDoing = position;
        holder.mImgImage.startAnimation();
        Log.i("0530", "handleClickItem: " + holder.mImgImage.getVisibility()+ " currentIndexOfDoing :" +position );
        util.disablePartControl();
        notifyDataSetChanged();
    }

    @Override
    public void onAnmiationEnd() {
        isDoingAction = false;
        currentIndexOfDoing = -1;
        util.enablePartControl();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allActions.size();
    }


    public void disableAction(){
        isDoingAction = true;
        notifyDataSetChanged();
    }

    public void enableActions(){
        isDoingAction = false;
        notifyDataSetChanged();
    }

    public class AvatarActionViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;

        private RoundProgressImageView mImgImage;

        private View mRoot;


        public AvatarActionViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mTextView = itemView.findViewById(R.id.tv_action);
            mImgImage = itemView.findViewById(R.id.iv_progress);
            mImgImage.setAnmiationListener(AvatarActionHAdapter.this);
        }


    }
}
