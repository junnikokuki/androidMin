package com.ubtechinc.alpha.mini.ui.strategy.circledialog.view;

import android.view.View;

/**
 * Created by hupei on 2018/5/21.
 */

public interface ButtonView {

    void regNegativeListener(View.OnClickListener onClickListener);

    void regPositiveListener(View.OnClickListener onClickListener);

    void regNeutralListener(View.OnClickListener onClickListener);

    void refreshText();

    View getView();

    boolean isEmpty();
}
