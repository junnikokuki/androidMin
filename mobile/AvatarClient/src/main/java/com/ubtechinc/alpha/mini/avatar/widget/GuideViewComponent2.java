package com.ubtechinc.alpha.mini.avatar.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blog.www.guideview.Component;
import com.ubtechinc.alpha.mini.avatar.R;


/**
 * @author：wululin
 * @date：2017/8/29 11:17
 * @modifier：ubt
 * @modify_date：2017/8/29 11:17
 * [A brief description]
 * version  头部新手引导
 */

public class GuideViewComponent2 implements Component {
    private RelativeLayout mGetItBtn;
    @Override
    public View getView(LayoutInflater inflater) {
        View view  = inflater.inflate(R.layout.avatar_guide_layout_2,null);
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
        return -100;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
