package com.ubtechinc.alpha.mini.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @Date: 2017/11/7.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UnBindDialogAdapter extends BaseAdapter {

    private List<CheckBindRobotModule.User> bindUserList;
    private Context ctx;
    private LayoutInflater inflater;

    private int currentMasterPosition = 0;

    public UnBindDialogAdapter(Context ctx, List<CheckBindRobotModule.User> bindUserList){
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.bindUserList = bindUserList;
    }

    public void setMasterSeleted(int position){
        currentMasterPosition = position;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (bindUserList != null && bindUserList.size() > 0){
            return bindUserList.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return bindUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_unbind_user, null);
            viewHolder.mAvatarImg = convertView.findViewById(R.id.iv_avatar);
            viewHolder.mBindUsername = convertView.findViewById(R.id.tv_username);
            viewHolder.mSelected = convertView.findViewById(R.id.iv_seleted);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(bindUserList.get(position).getUserImage())){
            Glide.with(ctx).load(bindUserList.get(position).getUserImage())
                    .placeholder(R.drawable.ic_robot_normal)
                    .bitmapTransform(new CropCircleTransformation((ctx)))
                    .crossFade(1000).into(viewHolder.mAvatarImg);
        }
        if (!TextUtils.isEmpty(bindUserList.get(position).getUserName())){
            viewHolder.mBindUsername.setText(bindUserList.get(position).getNickName());
        }
        if (currentMasterPosition == position){
            viewHolder.mSelected.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder{
        ImageView mAvatarImg;
        TextView mBindUsername;
        ImageView mSelected;
    }
}
