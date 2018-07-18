package com.ubtechinc.alpha.mini.ui.strategy.circledialog.view;

import android.view.View;
import android.widget.AdapterView;

import com.ubtechinc.alpha.mini.ui.strategy.circledialog.callback.OnRvItemClickListener;

/**
 * Created by hupei on 2018/4/18.
 */

public interface ItemsView {
    void refreshItems();

    void regOnItemClickListener(AdapterView.OnItemClickListener listener);

    void regOnItemClickListener(OnRvItemClickListener listener);

    View getView();
}
