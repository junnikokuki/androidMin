package com.ubtechinc.alpha.mini.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.utils.PopuUtils;

public class HomeMorePopupWindow extends PopupWindow implements View.OnClickListener{

    private Activity context;

    public HomeMorePopupWindow(final Activity context) {
        super(LayoutInflater.from(context).inflate(R.layout.popu_more, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = context;
        getContentView().findViewById(R.id.more_share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                PageRouter.toShareListActivity(context);
            }
        });
        getContentView().findViewById(R.id.more_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                PageRouter.toAlphaInfoActivity(context);
            }
        });
    }

    public void show(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        int left = anchor.getLeft();
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
