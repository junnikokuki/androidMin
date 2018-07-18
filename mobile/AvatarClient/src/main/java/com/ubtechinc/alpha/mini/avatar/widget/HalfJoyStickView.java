package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.utils.DensityUtil;

import static io.agora.rtc.internal.AudioRoutingController.TAG;


public class HalfJoyStickView extends View implements Runnable {
    // Constants
    private final double RAD = 57.2957795;
    public final static long DEFAULT_LOOP_INTERVAL = 1000; // 100 ms
    public final static int FRONT = 3;
    public final static int FRONT_RIGHT = 4;
    public final static int RIGHT = 1;
    public final static int LEFT = 5;
    public final static int LEFT_FRONT = 2;
    public final static int DEFAULT_DIRCATION = 0;
    // Variables
    private OnJoystickMoveListener onJoystickMoveListener; // Listener
    private Thread thread = new Thread(this);
    private long loopInterval = DEFAULT_LOOP_INTERVAL;
    private int xPosition = 0; // Touch x position
    private int yPosition = 0; // Touch y position
    private double centerX = 0; // Center view x position
    private double centerY = 0; // Center view y position
    private Paint mainCircle;
    private Paint secondaryCircle;
    private Paint button;
    private Paint horizontalLine;
    private Paint verticalLine;
    private int joystickRadius;
    private int buttonRadius;
    private int lastAngle = 0;
    private int lastPower = 0;
    private long repeatInterval = 1000;
    RectF temp;
    private int mWidth;
    private Paint paint;
    private Bitmap padBGBitmap;
    private Bitmap buttonBitmap;
    private int mHeight;
    private OnJoystickTouchListener onJoystickTouchListener;

    private boolean isLandSpcape = false;

    public HalfJoyStickView(Context context) {
        super(context);
    }

    public HalfJoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public HalfJoyStickView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initJoystickView();
    }

    protected void initJoystickView() {
        mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCircle.setColor(Color.WHITE);
        mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        secondaryCircle = new Paint();
        secondaryCircle.setColor(Color.GREEN);
        secondaryCircle.setStyle(Paint.Style.STROKE);

        verticalLine = new Paint();
        verticalLine.setStrokeWidth(5);
        verticalLine.setColor(Color.RED);

        horizontalLine = new Paint();
        horizontalLine.setStrokeWidth(2);
        horizontalLine.setColor(Color.BLACK);

        button = new Paint(Paint.ANTI_ALIAS_FLAG);
        button.setColor(Color.YELLOW);
        button.setStyle(Paint.Style.FILL);

        temp = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_default_h : R.drawable.ic_controller_foot_default);
        buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_controller_yellow);
    }

    public void setLandSpcape(boolean landSpcape) {
        isLandSpcape = landSpcape;
        padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_default_h : R.drawable.ic_controller_foot_default);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        xPosition = (int) getWidth() / 2;
        yPosition = (int) getHeight()/ 2;
        mWidth = xNew;
        mHeight = yNew;
        int d = Math.max(xNew, yNew);
        buttonRadius = (int) (d / 2 * 0.40);
        joystickRadius = (int) (d / 2 * 0.6);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and
        // height
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

        setMeasuredDimension(measure(widthMeasureSpec), d);

    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        centerX = (getWidth()) / 2;
        centerY  = (getHeight()) / 2;
        temp.set(0, 0,mWidth, (float) (centerY + DensityUtil.dp2px(getContext(),8)));
        canvas.drawBitmap(padBGBitmap, null, temp, paint);
        temp.set(xPosition- buttonRadius,yPosition - buttonRadius,xPosition + buttonRadius ,yPosition + buttonRadius);
        Log.i("half stickview2 0607", "onSizeChanged: left : "+ (xPosition- buttonRadius) + " top: "+ (yPosition - buttonRadius) + " right: "+ (xPosition + buttonRadius) + " bottom : "+ yPosition + buttonRadius);
        canvas.drawBitmap(buttonBitmap,null,temp,paint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return true;
        }
        xPosition = (int) event.getX();
        yPosition = (int) event.getY();

//        Log.i("wll","xPosition==========" + xPosition +  ";;yPosition=======" + yPosition  +  ";;;centerY======" + centerY);
        if(yPosition > centerY){
            yPosition = (int) centerY;
        }
        double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
                + (yPosition - centerY) * (yPosition - centerY));
        if (abs > joystickRadius) {
            xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
            yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
        }
        getAngle();
        changeBackagroud(getDirection());
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if(onJoystickTouchListener != null) {
                onJoystickTouchListener.onUp();
            }

            if (onJoystickMoveListener != null){
                xPosition = (int) centerX;
                yPosition = (int) centerY;
                thread.interrupt();
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_default_h : R.drawable.ic_controller_foot_default);
                invalidate();
                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());

            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(onJoystickTouchListener != null){
                onJoystickTouchListener.onDown();
            }
//            if (onJoystickMoveListener != null){
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
                thread = new Thread(this);
                thread.start();
//                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
//                            getDirection());
//            }

        }
        return true;
    }

    // TODO: 18/5/15 需要设置其透明度
    public void setViewEnable(boolean isEnable){
        buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_controller_yellow);
        setEnabled(isEnable);
        invalidate();
    }
    private int getAngle() {
        Log.i(TAG,"xPosition========" + xPosition + ";;yPosition=====" + yPosition + ";;centerX=====" + centerX + ";;centerY====" + centerY);
        if (xPosition > centerX) {
            if (yPosition < centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX))
                        * RAD + 90) + 1;
            } else if (yPosition > centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX)) * RAD) + 90;
            } else {
                return lastAngle = 90;
            }
        } else if (xPosition < centerX) {
            if (yPosition < centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX))
                        * RAD - 90) - 1;
            } else if (yPosition > centerY) {
                return lastAngle = (int) (Math.atan((yPosition - centerY)
                        / (xPosition - centerX)) * RAD) - 90;
            } else {
                return lastAngle = -90;
            }
        } else {
            if (yPosition < centerY) {
                return lastAngle = 1;
            } else if(yPosition == centerY){
                return lastAngle = 0;
            }else {
                if (lastAngle < 0) {
                    return lastAngle = -180;
                } else {
                    return lastAngle = 180;
                }
            }
        }
    }

    private int getPower() {
        return (int) (100 * Math.sqrt((xPosition - centerX)
                * (xPosition - centerX) + (yPosition - centerY)
                * (yPosition - centerY)) / joystickRadius);
    }

    private int getDirection() {
        if (lastPower == 0 && lastAngle == 0) {
            return 0;
        }
        int a = 0;
        if (lastAngle <= 0) {
            a = (lastAngle * -1) + 90;
        } else if (lastAngle > 0) {
            if (lastAngle <= 90) {
                a = 90 - lastAngle;
            } else {
                a = 360 - (lastAngle - 90);
            }
        }

        int direction = (int) (((a + 22) / 45) + 1);

        if (direction > 8) {
            direction = 1;
        }
        return direction;
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
                                          long repeatInterval) {
        this.onJoystickMoveListener = listener;
        this.loopInterval = repeatInterval;
    }

    public void setOnJoystickTouchListener(OnJoystickTouchListener listener){
        this.onJoystickTouchListener = listener;
    }

    public interface OnJoystickMoveListener {
        public void onValueChanged(int angle, int power, int direction);
    }
    public interface  OnJoystickTouchListener{
        void onDown();
        void onUp();
    }
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (onJoystickMoveListener != null)
                        onJoystickMoveListener.onValueChanged(getAngle(),
                                getPower(), getDirection());
                }
            });
            try {
                Thread.sleep(loopInterval);
            } catch (InterruptedException e) {
                break;
            }

        }
    }

    private void changeBackagroud(int direction) {
        switch (direction){
            case FRONT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_forward_h :  R.drawable.ic_controller_foot_forward );
                break;
            case FRONT_RIGHT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_315_degree_angle_h : R.drawable.ic_controller_foot_315_degree_angle);
                break;
            case LEFT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_turnleft_h : R.drawable.ic_controller_foot_turnleft);
                break;
            case LEFT_FRONT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_45_degree_angle_h : R.drawable.ic_controller_foot_45_degree_angle);
                break;
            case RIGHT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_turnright_h : R.drawable.ic_controller_foot_turnright);
                break;
            default:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), isLandSpcape ? R.drawable.ic_controller_foot_default_h : R.drawable.ic_controller_foot_default);
        }
    }
}
