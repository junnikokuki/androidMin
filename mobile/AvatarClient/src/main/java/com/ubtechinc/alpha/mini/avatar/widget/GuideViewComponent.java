package com.ubtechinc.alpha.mini.avatar.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blog.www.guideview.Component;
import com.ubtechinc.alpha.mini.avatar.R;

/**
 * @author：wululin
 * @date：2017/8/28 17:19
 * @modifier：ubt
 * @modify_date：2017/8/28 17:19
 * [A brief description]
 * version 脚步控制
 */

public class GuideViewComponent implements Component {
    private RelativeLayout mGetItBtn;
    @Override
    public View getView(LayoutInflater inflater) {
        View view  = inflater.inflate(R.layout.avatar_guide_layout_1,null);
        mGetItBtn = (RelativeLayout) view.findViewById(R.id.ll_get_it);
        return view;
    }

    public void setGetItListener(View.OnClickListener listener){
        mGetItBtn.setOnClickListener(listener);
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_CENTER;
    }

    @Override
    public int getXOffset() {
        return 110;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
