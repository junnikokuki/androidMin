package com.ubtechinc.alpha.mini.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;


/**
 * Created by drakeet on 9/28/14.
 */
public class CodingMaterialDialog {

    private final static int BUTTON_BOTTOM = 9;
    private final static int BUTTON_TOP = 9;

    private boolean mCancel;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private CodingMaterialDialog.Builder mBuilder;
    private View mView;
    private int mTitleResId;
    private CharSequence mTitle;
    private int mMessageResId;
    private int mMessageColorId;

    private CharSequence mMessage;
    private Button mPositiveButton;
    private LinearLayout.LayoutParams mLayoutParams;
    private Button mNegativeButton;
    private boolean mHasShow = false;
    private int mBackgroundResId = -1;
    private Drawable mBackgroundDrawable;
    private View mMessageContentView;
    private int mMessageContentViewResId;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private int pId = -1, nId = -1;
    private int pColor = -1, nColor = -1;
    private boolean isMessageBold = false;


    private String pText, nText;
    View.OnClickListener pListener, nListener;


    public CodingMaterialDialog(Context context) {
        this.mContext = context;
    }


    public void show() {
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        if (!mHasShow) {
            mBuilder = new Builder();
        } else {
            mAlertDialog.show();
        }
        mHasShow = true;
    }

    public boolean isShowing() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }

    public CodingMaterialDialog setView(View view) {
        mView = view;
        if (mBuilder != null) {
            mBuilder.setView(view);
        }
        return this;
    }


    public CodingMaterialDialog setContentView(View view) {
        mMessageContentView = view;
        mMessageContentViewResId = 0;
        if (mBuilder != null) {
            mBuilder.setContentView(mMessageContentView);
        }
        return this;
    }


    /**
     * Set a custom view resource to be the contents of the mDialog.
     *
     * @param layoutResId resource ID to be inflated
     */
    public CodingMaterialDialog setContentView(int layoutResId) {
        mMessageContentViewResId = layoutResId;
        mMessageContentView = null;
        if (mBuilder != null) {
            mBuilder.setContentView(layoutResId);
        }
        return this;
    }


    public CodingMaterialDialog setBackground(Drawable drawable) {
        mBackgroundDrawable = drawable;
        if (mBuilder != null) {
            mBuilder.setBackground(mBackgroundDrawable);
        }
        return this;
    }


    public CodingMaterialDialog setBackgroundResource(int resId) {
        mBackgroundResId = resId;
        if (mBuilder != null) {
            mBuilder.setBackgroundResource(mBackgroundResId);
        }
        return this;
    }


    public void dismiss() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }


    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public CodingMaterialDialog setTitle(int resId) {
        mTitleResId = resId;
        if (mBuilder != null) {
            mBuilder.setTitle(resId);
        }
        return this;
    }


    public CodingMaterialDialog setTitle(CharSequence title) {
        mTitle = title;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
        }
        return this;
    }


    public CodingMaterialDialog setMessage(int resId) {
        mMessageResId = resId;
        if (mBuilder != null) {
            mBuilder.setMessage(resId);
        }
        return this;
    }

    public CodingMaterialDialog setMessageColor(@ColorRes int resId) {
        mMessageColorId = resId;
        if (mBuilder != null) {
            mBuilder.setMessageColor(resId);
        }
        return this;
    }


    public CodingMaterialDialog setMessage(CharSequence message) {
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setMessage(message);
        }
        return this;
    }



    public CodingMaterialDialog setMessageBold(boolean isMessageBold){
        this.isMessageBold = isMessageBold;
        return this;
    }

    /**
     * Sets whether this mDialog is canceled when touched outside the window's
     * bounds OR pressed the back key. If setting to true, the mDialog is
     * set to be cancelable if not
     * already set.
     *
     * @param cancel Whether the mDialog should be canceled when touched outside
     *               the window OR pressed the back key.
     */
    public CodingMaterialDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mCancel = cancel;
        if (mBuilder != null) {
            mBuilder.setCanceledOnTouchOutside(mCancel);
        }
        return this;
    }


    public CodingMaterialDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }


    private class Builder {

        private TextView mTitleView;
        private ViewGroup mMessageContentRoot;
        private TextView mMessageView;
        private Window mAlertDialogWindow;
        private LinearLayout mButtonLayout;


        private Builder() {
            mAlertDialog = new AlertDialog.Builder(mContext).create();
            if (!((Activity) mContext).isFinishing()) {
                mAlertDialog.show();
            }

            mAlertDialog.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
            mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    Log.i("0116", "onKey: " + event.getAction());
                    if (keyCode == 4) {
                        mAlertDialog.dismiss();
                    }
                    return false;
                }
            });
            mAlertDialogWindow = mAlertDialog.getWindow();
            mAlertDialogWindow.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            View contentView = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_coding_dialog, null);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);

            mAlertDialogWindow.setBackgroundDrawableResource(R.drawable.material_dialog_window);

            mAlertDialogWindow.setContentView(contentView);

            mTitleView = (TextView) mAlertDialogWindow.findViewById(R.id.title);
            mMessageView = (TextView) mAlertDialogWindow.findViewById(R.id.message);
            mMessageContentRoot = (ViewGroup) mAlertDialogWindow.findViewById(
                    R.id.message_content_root);
            if (mView != null) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                        R.id.contentView);
                linearLayout.removeAllViews();
                linearLayout.addView(mView);
            }
            if (mTitleResId != 0) {
                setTitle(mTitleResId);
            }
            if (mTitle != null) {
                setTitle(mTitle);
            }
            if (mTitle == null && mTitleResId == 0) {
                mTitleView.setVisibility(View.GONE);
            }
            if (mMessageResId != 0) {
                setMessage(mMessageResId);
            }
            if (mMessageColorId != 0) {
                setMessageColor(mMessageColorId);
            }
            if (mMessage != null) {
                setMessage(mMessage);
            }
            if (isMessageBold){
                setMessageBold(isMessageBold);
            }

//            if (pId != -1) {
//                mPositiveButton.setVisibility(View.VISIBLE);
//                mPositiveButton.setText(pId);
//                mPositiveButton.setOnClickListener(pListener);
//                if (isLollipop()) {
//                    mPositiveButton.setElevation(0);
//                }
//            }
//            if (nId != -1) {
//                mNegativeButton.setVisibility(View.VISIBLE);
//                mNegativeButton.setText(nId);
//                mNegativeButton.setOnClickListener(nListener);
//                if (isLollipop()) {
//                    mNegativeButton.setElevation(0);
//                }
//            }
//            if (!isNullOrEmpty(pText)) {
//                mPositiveButton.setVisibility(View.VISIBLE);
//                mPositiveButton.setText(pText);
//                mPositiveButton.setOnClickListener(pListener);
//                if (isLollipop()) {
//                    mPositiveButton.setElevation(0);
//                }
//            }
//
//            if (!isNullOrEmpty(nText)) {
//                mNegativeButton.setVisibility(View.VISIBLE);
//                mNegativeButton.setText(nText);
//                mNegativeButton.setOnClickListener(nListener);
//                if (isLollipop()) {
//                    mNegativeButton.setElevation(0);
//                }
//            }
//            if (pColor != -1) {
//                ColorStateList whiteColor = mContext.getResources().getColorStateList(pColor);
//                mPositiveButton.setTextColor(whiteColor);
//            }
//
//            if (nColor != -1) {
//                ColorStateList whiteColor = mContext.getResources().getColorStateList(nColor);
//                mNegativeButton.setTextColor(whiteColor);
//            }
//
//
//            if (isNullOrEmpty(pText) && pId == -1) {
//                mPositiveButton.setVisibility(View.GONE);
//            }
//            if (isNullOrEmpty(nText) && nId == -1) {
//                mNegativeButton.setVisibility(View.GONE);
//            }
            if (mBackgroundResId != -1) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                        R.id.material_background);
                linearLayout.setBackgroundResource(mBackgroundResId);
            }
            if (mBackgroundDrawable != null) {
                LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                        R.id.material_background);
                linearLayout.setBackground(mBackgroundDrawable);
            }

            if (mMessageContentView != null) {
                this.setContentView(mMessageContentView);
            } else if (mMessageContentViewResId != 0) {
                this.setContentView(mMessageContentViewResId);
            }
            mAlertDialog.setCanceledOnTouchOutside(mCancel);
            mAlertDialog.setCancelable(mCancel);
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(mOnDismissListener);
            }
        }


        public void setTitle(int resId) {
            mTitleView.setText(resId);
            mTitleView.setVisibility(View.VISIBLE);
        }


        public void setTitle(CharSequence title) {
            if (title != null) {
                mTitleView.setText(title);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setText("");
                mTitleView.setVisibility(View.GONE);
            }
        }

        public void setMessageBold(boolean isBold){
            if (mMessageView != null) {
                mMessageView.getPaint().setFakeBoldText(isBold);
            }
        }


        public void setMessage(int resId) {
            if (mMessageView != null) {
                mMessageView.setText(resId);
            }
        }

        public void setMessageColor(@ColorRes int color) {
            if (mMessageView != null) {
                mMessageView.setTextColor(ContextCompat.getColor(mContext, color));
            }
        }


        public void setMessage(CharSequence message) {
            if (mMessageView != null) {
                mMessageView.setText(message);
            }
        }


//        /**
//         * set positive button
//         *
//         * @param text the name of button
//         */
//        public void setPositiveButton(String text, final View.OnClickListener listener) {
//            Button button = new Button(mContext);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            button.setLayoutParams(params);
//            button.setBackgroundResource(R.drawable.material_card);
//            button.setTextColor(Color.argb(255, 35, 159, 242));
//            button.setText(text);
//            button.setGravity(Gravity.CENTER);
//            button.setTextSize(14);
//            button.setPadding(dip2px(12), 0, dip2px(32), dip2px(BUTTON_BOTTOM));
//            button.setOnClickListener(listener);
//            mPositiveButton = button;
//            mButtonLayout.addView(button);
//        }
//
//
//        /**
//         * set negative button
//         *
//         * @param text the name of button
//         */
//        public void setNegativeButton(String text, final View.OnClickListener listener) {
//            Button button = new Button(mContext);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            button.setLayoutParams(params);
//            button.setBackgroundResource(R.drawable.material_card);
//            button.setText(text);
//            button.setTextColor(Color.argb(222, 0, 0, 0));
//            button.setTextSize(14);
//            button.setGravity(Gravity.CENTER);
//            button.setPadding(0, 0, 0, dip2px(8));
//            button.setOnClickListener(listener);
//            if (mButtonLayout.getChildCount() > 0) {
//                params.setMargins(20, 0, 10, dip2px(BUTTON_BOTTOM));
//                button.setLayoutParams(params);
//                mButtonLayout.addView(button, 1);
//            } else {
//                button.setLayoutParams(params);
//                mButtonLayout.addView(button);
//            }
//            mNegativeButton = button;
//        }
//

        public void setView(View view) {
            LinearLayout l = (LinearLayout) mAlertDialogWindow.findViewById(R.id.contentView);
            l.removeView(mMessageView);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);

            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mAlertDialogWindow.setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    // show imm
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });

            l.addView(view);

            if (view instanceof ViewGroup) {

                ViewGroup viewGroup = (ViewGroup) view;

                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof EditText) {
                        EditText editText = (EditText) viewGroup.getChildAt(i);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                    }
                }
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) viewGroup
                                .getChildAt(i);
                        autoCompleteTextView.setFocusable(true);
                        autoCompleteTextView.requestFocus();
                        autoCompleteTextView.setFocusableInTouchMode(true);
                    }
                }
            }
        }


        public void setContentView(View contentView) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(layoutParams);
            if (contentView instanceof ListView) {
                setListViewHeightBasedOnChildren((ListView) contentView);
            }
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                    R.id.material_background);
            if (linearLayout != null) {
                linearLayout.removeAllViews();
                linearLayout.addView(contentView);
            }
            for (int i = 0; i < (linearLayout != null ? linearLayout.getChildCount() : 0); i++) {
                if (linearLayout.getChildAt(i) instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView
                            = (AutoCompleteTextView) linearLayout.getChildAt(i);
                    autoCompleteTextView.setFocusable(true);
                    autoCompleteTextView.requestFocus();
                    autoCompleteTextView.setFocusableInTouchMode(true);
                }
            }
        }


        /**
         * Set a custom view resource to be the contents of the mDialog. The
         * resource will be inflated into a ScrollView.
         *
         * @param layoutResId resource ID to be inflated
         */
        public void setContentView(int layoutResId) {
            mMessageContentRoot.removeAllViews();
            // Not setting this to the other content view because user has defined their own
            // layout params, and we don't want to overwrite those.
            LayoutInflater.from(mMessageContentRoot.getContext())
                    .inflate(layoutResId, mMessageContentRoot);
        }


        public void setBackground(Drawable drawable) {
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                    R.id.material_background);
            linearLayout.setBackground(drawable);
        }


        public void setBackgroundResource(int resId) {
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(
                    R.id.material_background);
            linearLayout.setBackgroundResource(resId);
        }


        public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            mAlertDialog.setCancelable(canceledOnTouchOutside);
        }
    }


    private boolean isNullOrEmpty(String nText) {
        return nText == null || nText.isEmpty();
    }


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}

