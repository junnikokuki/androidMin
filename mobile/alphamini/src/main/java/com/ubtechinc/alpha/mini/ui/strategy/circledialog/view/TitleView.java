package com.ubtechinc.alpha.mini.ui.strategy.circledialog.view;

import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;

import com.ubtechinc.alpha.mini.ui.strategy.circledialog.CircleParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.DialogParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.SubTitleParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.TitleParams;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.resource.CircleDrawable;


/**
 * 对话框标题
 * Created by hupei on 2017/3/29.
 */
final class TitleView extends ScaleLinearLayout {

    public TitleView(Context context, CircleParams params) {
        super(context);
        init(params);
    }

    private void init(CircleParams params) {

        setOrientation(LinearLayout.VERTICAL);

        DialogParams dialogParams = params.dialogParams;
        TitleParams titleParams = params.titleParams;
        SubTitleParams subTitleParams = params.subTitleParams;

        //标题
        final ScaleTextView tvTitle = new ScaleTextView(getContext());
        setTitleBg(tvTitle, params, titleParams.backgroundColor
                , dialogParams.backgroundColor, dialogParams.radius);
        tvTitle.setGravity(titleParams.gravity);
        tvTitle.setHeight(titleParams.height);
        tvTitle.setTextColor(titleParams.textColor);
        tvTitle.setTextSize(titleParams.textSize);
        tvTitle.setText(titleParams.text);
        addView(tvTitle);

        //副标题
        if (subTitleParams != null) {
            final ScaleTextView tvSubTitle = new ScaleTextView(getContext());
            setSubTitleBg(tvSubTitle, subTitleParams.backgroundColor, dialogParams.backgroundColor);
            tvSubTitle.setGravity(subTitleParams.gravity);
            if (subTitleParams.height != 0)
                tvSubTitle.setHeight(subTitleParams.height);
            tvSubTitle.setTextColor(subTitleParams.textColor);
            tvSubTitle.setTextSize(subTitleParams.textSize);
            tvSubTitle.setText(subTitleParams.text);
            int[] padding = subTitleParams.padding;
            if (padding != null)
                tvSubTitle.setAutoPadding(padding[0], padding[1], padding[2], padding[3]);
            addView(tvSubTitle);
        }
    }

    private void setTitleBg(ScaleTextView tv, CircleParams params, int tbg, int dbg, int radius) {
        //如果标题没有背景色，则使用默认色
        int bg = tbg != 0 ? tbg : dbg;

        //有内容则顶部圆角
        if (params.textParams != null || params.itemsParams != null || params.progressParams != null
                || params.inputParams != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tv.setBackground(new CircleDrawable(bg, radius, radius, 0, 0));
            } else {
                tv.setBackgroundDrawable(new CircleDrawable(bg, radius, radius, 0, 0));
            }
        }
        //无内容则全部圆角
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tv.setBackground(new CircleDrawable(bg, radius));
            } else {
                tv.setBackgroundDrawable(new CircleDrawable(bg, radius));
            }
        }
    }

    private void setSubTitleBg(ScaleTextView tv, int tbg, int dbg) {
        //如果标题没有背景色，则使用默认色
        int bg = tbg != 0 ? tbg : dbg;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tv.setBackground(new CircleDrawable(bg, 0));
        } else {
            tv.setBackgroundDrawable(new CircleDrawable(bg, 0));
        }
    }
}
