package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;

/**
 * @作者：liudongyang
 * @日期: 18/7/11 16:30
 * @描述: popwindow
 */
public class WordsPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mCtx;

    private IDeleteWordsCallback mDeleteCallback;

    private int mDeleteIndex;

    private TextView tvDelete;

    private String TAG = getClass().getSimpleName();

    public WordsPopWindow(Context context, int position, IDeleteWordsCallback callback) {
        super(LayoutInflater.from(context).inflate(R.layout.pop_car_delete_words, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mCtx = context;
        this.mDeleteIndex = position;
        tvDelete = getContentView().findViewById(R.id.tv_delete_words);
        getContentView().setOnClickListener(this);
        mDeleteCallback = callback;
    }

    private boolean mIsPoliceMode;
    public void setMode(boolean isPoliceMode) {
        this.mIsPoliceMode = isPoliceMode;
    }

    public void show(View anchor, int xoff, int yoff) {
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
        if (mIsPoliceMode) {
            tvDelete.setBackground(mCtx.getResources().getDrawable(R.drawable.ic_delete_car_words_red));
        } else {
            tvDelete.setBackground(mCtx.getResources().getDrawable((R.drawable.ic_delete_car_words)));
        }
        showAsDropDown(anchor, xoff, yoff);
    }


    @Override
    public void onClick(View v) {
        dismiss();
        mDeleteCallback.onDelete(mDeleteIndex);
    }

    public interface IDeleteWordsCallback {
        void onDelete(int deleteIndex);
    }
}
