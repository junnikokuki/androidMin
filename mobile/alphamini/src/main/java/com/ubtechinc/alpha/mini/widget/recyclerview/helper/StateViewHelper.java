package com.ubtechinc.alpha.mini.widget.recyclerview.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ubtechinc.alpha.mini.widget.recyclerview.BaseItemAdapter;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.BaseItemState;


/**
 * 状态页（如空白错误页等）辅助类
 * <p>
 * 状态页展示时会作为RecyclerView的唯一的Item展示在界面中
 * 注意：需要在RecyclerView设置完adapter后在初始化本实例
 * Created by free46000 on 2017/4/23.
 */
public class StateViewHelper {
    private RecyclerView recyclerView;
    private BaseItemState itemState;

    private RecyclerView.Adapter dataAdapter;
    private BaseItemAdapter stateAdapter;
    private RecyclerView.LayoutManager mManager;
    private boolean isShowing;

    /**
     * 需要在RecyclerView设置完adapter后在初始化本实例
     *
     * @param recyclerView
     * @param itemState
     */
    public StateViewHelper(@NonNull RecyclerView recyclerView, @NonNull BaseItemState itemState) {
        this.recyclerView = recyclerView;
        //记住RecyclerView初始的Adapter
        this.dataAdapter = recyclerView.getAdapter();
        mManager = recyclerView.getLayoutManager();
        if (dataAdapter == null) {
            throw new IllegalArgumentException("请在设置完adapter后在初始化本实例！");
        }
        this.itemState = itemState;
    }

    /**
     * 展示状态页
     * <p>
     * 为RecyclerView设置新的stateAdapter，本adapter中保存唯一的状态页Item{@link BaseItemState}
     */
    public void show() {
        if (stateAdapter != null) {
            return;
        }
        isShowing = true;
        stateAdapter = new BaseItemAdapter();
        stateAdapter.addDataItem(itemState);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(stateAdapter);
    }

    /**
     * 隐藏状态页
     * <p>
     * 将RecyclerView的Adapter设置为初始化时记住的Adapter
     */
    public void hide() {
        if (dataAdapter == null) {
            return;
        }
        isShowing = false;
        stateAdapter = null;
        recyclerView.setLayoutManager(mManager);
        recyclerView.setAdapter(dataAdapter);
    }

    public boolean isShowing() {
        return isShowing;
    }


}
