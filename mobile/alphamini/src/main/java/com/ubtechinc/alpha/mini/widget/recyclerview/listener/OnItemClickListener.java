package com.ubtechinc.alpha.mini.widget.recyclerview.listener;

import android.view.View;

import com.ubtechinc.alpha.mini.widget.recyclerview.holder.BaseViewHolder;


/**
 * Item点击监听类
 *
 * @author free46000
 */
public abstract class OnItemClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        BaseViewHolder viewHolder = ListenerUtil.getViewHolderByItemView(v);
        if (viewHolder == null) {
            return;
        }

        onItemClick(viewHolder, v);
    }

    /**
     * 点击回调 可以通过viewHolder get到需要的数据
     */
    public abstract void onItemClick(BaseViewHolder viewHolder, View v);


}
