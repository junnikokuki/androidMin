package com.ubtechinc.alpha.mini.ui.bind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @Date: 2018/2/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UnbindSlaverAdapter extends RecyclerView.Adapter<UnbindSlaverAdapter.UnbindSlaveHolder> {

    private List<CheckBindRobotModule.User> mSlaverList = new ArrayList<>();

    private LayoutInflater mInflater;

    private Context mContext;

    private int mSelectedIndex = 0;

    public int getmSelectedIndex() {
        return mSelectedIndex;
    }

    public UnbindSlaverAdapter(Context ctx, List<CheckBindRobotModule.User> users){
        this.mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mSlaverList.addAll(users);
    }

    @Override
    public UnbindSlaveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_unbind_user, null);
        return new UnbindSlaveHolder(view);
    }

    @Override
    public void onBindViewHolder(UnbindSlaveHolder holder,final int position) {
        Glide.with(mContext).load(mSlaverList.get(position).getUserImage())
                .placeholder(R.drawable.ic_robot_normal)
                .bitmapTransform(new CropCircleTransformation((mContext)))
                .crossFade(1000).into(holder.mImgAvater);

        if (!TextUtils.isEmpty(mSlaverList.get(position).getUserName())){
            holder.mTextName.setText(mSlaverList.get(position).getNickName());
        }
        if (mSelectedIndex == position){
            holder.mImgSelected.setVisibility(View.VISIBLE);
        }else {
            holder.mImgSelected.setVisibility(View.GONE);
        }

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != mSelectedIndex){
                    int temp = mSelectedIndex;
                    mSelectedIndex = position;
                    notifyItemChanged(temp);
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSlaverList.size();
    }

    public class UnbindSlaveHolder extends RecyclerView.ViewHolder{
        private ImageView mImgSelected;
        private ImageView mImgAvater;
        private TextView  mTextName;
        private View      mItemView;

        public UnbindSlaveHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mImgSelected = itemView.findViewById(R.id.iv_seleted);
            mImgAvater   = itemView.findViewById(R.id.iv_avatar);
            mTextName    = itemView.findViewById(R.id.tv_username);
        }
    }
}
