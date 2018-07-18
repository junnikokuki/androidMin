package com.ubtechinc.alpha.mini.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ubtech.utilcode.utils.SizeUtils;
import com.ubtechinc.alpha.mini.R;

/**
 * Created by ubt on 2018/4/8.
 */
public class ProgressBtn extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;

    //背景画笔
    private Paint mBackgroundPaint;
    //按钮文字画笔
    private volatile Paint mTextPaint;

    private Paint mBackgroundBorderPaint;

    //背景颜色
    private int mBackgroundColor;
    //下载中后半部分后面背景颜色
    private int mBackgroundSecondColor;
    //文字颜色
    private int mTextColor;
    //覆盖后颜色
    private int mTextCoverColor;


    private float mProgress = -1;
    private float mToProgress;
    private int mMaxProgress;
    private int mMinProgress;
    private float mProgressPercent;

    private float mButtonRadius;


    private RectF mBackgroundBounds;
    private LinearGradient mProgressBgGradient;
    private LinearGradient mProgressTextGradient;

    //下载平滑动画
    private ValueAnimator mProgressAnimation;

    //记录当前文字
    private CharSequence mCurrentText;

    private int mState;

    public ProgressBtn(Context context) {
        this(context, null);

    }

    public ProgressBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mContext = context;
            initAttrs(context, attrs);
            init();
            setupAnimations();
        }

    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimDownloadProgressButton);
        mBackgroundColor = a.getColor(R.styleable.AnimDownloadProgressButton_progressbtn_backgroud_color, Color.parseColor("#6699ff"));
        mBackgroundSecondColor = a.getColor(R.styleable.AnimDownloadProgressButton_progressbtn_backgroud_second_color, Color.LTGRAY);
        mButtonRadius = a.getDimension(R.styleable.AnimDownloadProgressButton_progressbtn_radius, getMeasuredHeight() / 2);
        mTextColor = a.getColor(R.styleable.AnimDownloadProgressButton_progressbtn_text_color, mBackgroundColor);
        mTextCoverColor = a.getColor(R.styleable.AnimDownloadProgressButton_progressbtn_text_covercolor, Color.WHITE);
        a.recycle();
    }

    private void init() {

        mMaxProgress = 100;
        mMinProgress = 0;
        mProgress = 0;


        //设置背景画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);


        mBackgroundBorderPaint = new Paint();
        mBackgroundBorderPaint.setStyle(Paint.Style.STROKE);
        mBackgroundBorderPaint.setColor(mBackgroundColor);
        mBackgroundBorderPaint.setStrokeWidth(SizeUtils.dp2px(1));

        //设置文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(50f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //解决文字有时候画不出问题
            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint);
        }
        invalidate();
    }


    private void setupAnimations() {

        //ProgressBar的动画
        mProgressAnimation = ValueAnimator.ofFloat(0, 1).setDuration(500);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float timepercent = (float) animation.getAnimatedValue();
                mProgress = ((mToProgress - mProgress) * timepercent + mProgress);
                invalidate();
            }
        });


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            drawing(canvas);
        }
    }

    private void drawing(Canvas canvas) {
        drawBackground(canvas);
        drawTextAbove(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mBackgroundBounds = new RectF();
        if (mButtonRadius == 0) {
            mButtonRadius = getMeasuredHeight() / 2;
        }
        mBackgroundBounds.left = 2;
        mBackgroundBounds.top = 2;
        mBackgroundBounds.right = getMeasuredWidth() - 2;
        mBackgroundBounds.bottom = getMeasuredHeight() - 2;
        //color

        mProgressPercent = mProgress / (mMaxProgress + 0f);
        mProgressBgGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                new int[]{mBackgroundColor, mBackgroundSecondColor},
                new float[]{mProgressPercent, mProgressPercent + 0.001f},
                Shader.TileMode.CLAMP
        );
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setShader(mProgressBgGradient);
        canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);


        canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundBorderPaint);

    }

    private void drawTextAbove(Canvas canvas) {
        final float y = canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mCurrentText == null) {
            mCurrentText = "";
        }
        final float textWidth = mTextPaint.measureText(mCurrentText.toString());
        //进度条压过距离
        float coverlength = getMeasuredWidth() * mProgressPercent;
        //开始渐变指示器
        float indicator1 = getMeasuredWidth() / 2 - textWidth / 2;
        //结束渐变指示器
        float indicator2 = getMeasuredWidth() / 2 + textWidth / 2;
        //文字变色部分的距离
        float coverTextLength = textWidth / 2 - getMeasuredWidth() / 2 + coverlength;
        float textProgress = coverTextLength / textWidth;
        if (coverlength <= indicator1) {
            mTextPaint.setShader(null);
            mTextPaint.setColor(mTextColor);
        } else if (indicator1 < coverlength && coverlength <= indicator2) {
            mProgressTextGradient = new LinearGradient((getMeasuredWidth() - textWidth) / 2, 0, (getMeasuredWidth() + textWidth) / 2, 0,
                    new int[]{mTextCoverColor, mTextColor},
                    new float[]{textProgress, textProgress + 0.001f},
                    Shader.TileMode.CLAMP);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setShader(mProgressTextGradient);
        } else {
            mTextPaint.setShader(null);
            mTextPaint.setColor(mTextCoverColor);
        }
        canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);


    }


    /**
     * 设置带下载进度的文字
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setProgressText(String text, float progress) {
        if (progress >= mMinProgress && progress <= mMaxProgress) {
            mCurrentText = text ;
            mToProgress = progress;
            if (mProgressAnimation.isRunning()) {
                mProgressAnimation.resume();
                mProgressAnimation.start();
            } else {
                mProgressAnimation.start();
            }
        } else if (progress < mMinProgress) {
            mProgress = 0;
        } else if (progress > mMaxProgress) {
            mProgress = 100;
            mCurrentText = text;
            invalidate();
        }
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;

    }

    public float getButtonRadius() {
        return mButtonRadius;
    }

    public void setButtonRadius(float buttonRadius) {
        mButtonRadius = buttonRadius;
    }

    public int getTextColor() {
        return mTextColor;
    }

    @Override
    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getTextCoverColor() {
        return mTextCoverColor;
    }

    public void setTextCoverColor(int textCoverColor) {
        mTextCoverColor = textCoverColor;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public void setMinProgress(int minProgress) {
        mMinProgress = minProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mState = ss.state;
        mProgress = ss.progress;
        mCurrentText = ss.currentText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, (int) mProgress, mState, mCurrentText != null?mCurrentText.toString():"");
    }

    public static class SavedState extends BaseSavedState {

        private int progress;
        private int state;
        private String currentText;

        public SavedState(Parcelable parcel, int progress, int state, String currentText) {
            super(parcel);
            this.progress = progress;
            this.state = state;
            this.currentText = currentText;
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            state = in.readInt();
            currentText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeInt(state);
            out.writeString(currentText);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


}
