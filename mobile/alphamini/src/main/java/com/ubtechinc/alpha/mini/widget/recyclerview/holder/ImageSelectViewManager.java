package com.ubtechinc.alpha.mini.widget.recyclerview.holder;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.widget.recyclerview.item.Image;

/**
 * Created by junsheng.chen on 2018/7/11.
 */
public class ImageSelectViewManager extends ViewHolderManager<Image, MultiViewHolder> {

    @NonNull
    @Override
    public MultiViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        MultiViewHolder viewHolder = new MultiViewHolder(getItemView(parent));
        onCreateViewHolder(viewHolder);
        return viewHolder;
    }

    protected void onCreateViewHolder(@NonNull MultiViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(MultiViewHolder holder, Image image) {

    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }
}
