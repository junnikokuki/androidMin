package com.ubtechinc.alpha.mini.ui.title;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;

public class TitleBackItemView extends FrameLayout {

    Context mContext;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private int mIconBack;
    private String mTitle;

    public TitleBackItemView(Context context) {
        super(context);
        initView(context,null);
    }

    public TitleBackItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public TitleBackItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void initView(Context context, AttributeSet attrs){
        mContext=context;
        LayoutInflater.from(getContext()).inflate(R.layout.title_back_item_view,this,true);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        if(attrs!=null){
            //获取自定义属性值
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.title_back_item);
            mIconBack = ta.getResourceId(R.styleable.title_back_item_iconBack,R.drawable.ic_back);
            mTitle = ta.getString(R.styleable.title_back_item_title);
            ta.recycle();
        }
        findViews();
        initViews();
    }

    public void setTextColor(int color) {
        mTvTitle.setTextColor(color);
    }

    private void findViews() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.ad_title);
    }

    public void setIvBack(@DrawableRes int resid) {
        mIvBack.setImageResource(resid);
    }

    private void initViews() {
        mIvBack.setImageResource(mIconBack);
        mTvTitle.setText(mTitle);
    }

    public void setTitle(@StringRes int id) {
        mTvTitle.setText(getContext().getString(id));
    }

    public void setOnBackListener(OnClickListener listener){
        mIvBack.setOnClickListener(listener);
    }

    public void hideUnderLine() {
        findViewById(R.id.under_line).setVisibility(GONE);
    }
}
