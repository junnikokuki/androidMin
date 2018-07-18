package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ItemModifyAvatarBinding;

/**
 * @Date: 2017/12/20.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 好友头像
 */

public class FriendAvatarAdapter extends RecyclerView.Adapter<FriendAvatarAdapter.FriendHolder> {

    private Context mCtx;

    private int selectIndex = 0;

    public FriendAvatarAdapter(Context ctx) {
        mCtx = ctx;
    }

    public int getSelectIndex(){
        return selectIndex;
    }

    public void setAvatars(int totalcout){

    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemModifyAvatarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mCtx), R.layout.item_modify_avatar, null, false);
        return new FriendHolder(binding);
    }

    @Override
    public void onBindViewHolder(FriendHolder holder, final int position) {
        if (position == selectIndex){
            holder.binding.setIsChecked(true);
        }else{
            holder.binding.setIsChecked(false);
        }
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectIndex);
                selectIndex = position;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public class FriendHolder extends RecyclerView.ViewHolder{

        private ItemModifyAvatarBinding binding;

        public FriendHolder(ItemModifyAvatarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
