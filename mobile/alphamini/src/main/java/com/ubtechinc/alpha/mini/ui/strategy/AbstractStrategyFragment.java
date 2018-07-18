package com.ubtechinc.alpha.mini.ui.strategy;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by junsheng.chen on 2018/6/7.
 */
public abstract class AbstractStrategyFragment extends Fragment {

    public abstract boolean onKeyDown(int keyCode, KeyEvent event);

    public abstract String getFragmentTag();

    public abstract boolean onBackPressed();

}
