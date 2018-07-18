package com.ubtechinc.alpha.mini.widget.viewpage.page;

import android.util.SparseArray;

/**
 * Created by junsheng.chen on 2018/7/9.
 */
public interface PageData<T> {

    SparseArray<T> selectedList();

    void updateList(SparseArray<T> list);
}
