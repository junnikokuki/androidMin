package com.ubtechinc.alpha.mini.widget.recyclerview.item;

import android.support.annotation.NonNull;

import com.ubtechinc.alpha.mini.widget.recyclerview.holder.ViewHolderManager;


/**
 * 自定义Item管理类 可以获取item类型标识和ViewHolderManager
 * Created by free46000 on 2017/3/26.
 */
public interface ItemManager {
    @NonNull
    String getItemTypeName();

    /**
     * 返回自定义的ViewHolderManager，若为null则从根据本class查找注册的ViewHolderManager
     *
     * @return 返回自定义的ViewHolderManager
     */
    ViewHolderManager getViewHolderManager();
}
