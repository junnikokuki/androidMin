package com.ubtechinc.alpha.mini.widget.viewpage.page;

import android.util.SparseArray;

import com.ubtechinc.alpha.mini.widget.recyclerview.item.Image;

/**
 * Created by junsheng.chen on 2018/7/9.
 */
public class SelectPageData implements PageData<Image> {

    private SparseArray<Image> mData;

    public SelectPageData() {
        mData = new SparseArray<>(8);
    }

    @Override
    public SparseArray selectedList() {
        return mData;
    }

    @Override
    public void updateList(SparseArray<Image> list) {
        mData = list;
    }
}
