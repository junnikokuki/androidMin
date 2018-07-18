package com.ubtechinc.alpha.mini.ui.car;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by riley.zhang on 2018/6/25.
 */

public class CarWheelAdapter extends PagerAdapter {
    private ArrayList<View> mViewList = new ArrayList<>();

    public void setData(ArrayList<View> views) {
        mViewList = views;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }
}
