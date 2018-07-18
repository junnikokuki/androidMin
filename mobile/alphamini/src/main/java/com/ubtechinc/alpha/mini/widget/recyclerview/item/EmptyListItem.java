package com.ubtechinc.alpha.mini.widget.recyclerview.item;

import android.databinding.ViewDataBinding;

import com.ubtechinc.alpha.mini.BR;
import com.ubtechinc.alpha.mini.R;


/**
 * Created by junsheng.chen on 2018/7/6.
 */
public class EmptyListItem extends BaseItemState<EmptyListItem> {

    @Override
    protected void onBindViewHolder(ViewDataBinding dataBinding, EmptyListItem data) {
        dataBinding.setVariable(BR.itemData, data);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_empty_item;
    }
}
