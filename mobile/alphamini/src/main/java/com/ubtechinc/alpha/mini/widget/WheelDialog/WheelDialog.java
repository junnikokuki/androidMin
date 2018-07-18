package com.ubtechinc.alpha.mini.widget.WheelDialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtechinc.alpha.mini.R;

import java.util.ArrayList;

/**
 * Created by riley.zhang on 2018/6/11.
 */

public class WheelDialog extends Dialog {

    public WheelDialog(@NonNull Context context) {
        this(context, 0);
    }

    public WheelDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WheelDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        private Context mContext;
        private ArrayList<String> mDataList = new ArrayList<>();
        private OnItemClickListener mCancelClickListener;
        private OnItemClickListener mSureClickListener;
        private boolean mCancelable = false;
        private boolean mCancelableOutside = false;
        private String mSelectedSerialNum;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setSelectedSerialNum(String selectedSerialNum) {
            mSelectedSerialNum = selectedSerialNum;
            return this;
        }

        public Builder setData(ArrayList<String> dataList) {
            mDataList = dataList;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setCancelableOutside(boolean cancelable) {
            mCancelableOutside = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnItemClickListener itemClickListener) {
            mCancelClickListener = itemClickListener;
            return this;
        }

        public Builder setOnSureListener(OnItemClickListener itemClickListener) {
            mSureClickListener = itemClickListener;
            return this;
        }

        public WheelDialog create() {
            final WheelDialog dialog = new WheelDialog(mContext, R.style.Theme_Light_NoTitle_Dialog);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_wheel_dialog, null);
            final LoopView loopView = view.findViewById(R.id.loopview);
//            for (int i = 0; i < 5; i++) {
//                mDataList.add(i+"111111111");
//            }

            loopView.setItems(mDataList);
            int selectPosition = 0;
            //让滚轮高亮到当前绑定的机器人的序列号
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).equalsIgnoreCase(mSelectedSerialNum)) {
                    selectPosition = i;
                    break;
                }
            }
            loopView.setInitPosition(selectPosition);
            loopView.setNotLoop();

            TextView cancel = view.findViewById(R.id.text_wheel_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = loopView.getSelectedItem();
                    mCancelClickListener.onClick(dialog, position);
                }
            });

            TextView sure = view.findViewById(R.id.text_wheel_sure);
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = loopView.getSelectedItem();
                    mSureClickListener.onClick(dialog, position);
                }
            });
            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = 500;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCancelable(mCancelable);
            dialog.setCanceledOnTouchOutside(mCancelableOutside);

            return dialog;
        }
    }

    public interface OnItemClickListener{
        void onClick(WheelDialog dialog, int position);
    }
}
