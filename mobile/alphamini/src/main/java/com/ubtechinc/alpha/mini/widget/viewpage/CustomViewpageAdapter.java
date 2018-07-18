package com.ubtechinc.alpha.mini.widget.viewpage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;

import java.util.ArrayList;

/**
 * Created by junsheng.chen on 2018/7/7.
 */
public class CustomViewpageAdapter extends PagerAdapter {

    private ArrayList<View> mViews;
    private Context mContext;

    public CustomViewpageAdapter(Context context) {
        mContext = context;
        mViews = new ArrayList<>();
    }

    public void addItemView(View view) {
        mViews.add(view);
    }

    public View getItem(int position) {
        return mViews.get(position);
    }

    public int indexOfView(View view) {
        return mViews.indexOf(view);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getStringArray(R.array.upload_titles)[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
