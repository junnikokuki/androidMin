package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.R;

public class JoyStickView extends View implements Runnable {
    // Constants
    private final double RAD = 57.2957795;
    public final static long DEFAULT_LOOP_INTERVAL = 1000; // 100 ms
    public final static int FRONT = 3;
    public final static int FRONT_RIGHT = 4;
    public final static int RIGHT = 5;
    public final static int RIGHT_BOTTOM = 6;
    public final static int BOTTOM = 7;
    public final static int BOTTOM_LEFT = 8;
    public final static int LEFT = 1;
    public final static int LEFT_FRONT = 2;
    public final static int DEFAULT_DIRCATION = 0;
    // Variables
    private OnJoystickMoveListener onJoystickMoveListener; // Listener
    private OnJoystickTouchListener onJoystickTouchListener;
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
    RectF temp;
    private int mWidth;
    private Paint paint;
    private Bitmap padBGBitmap;
    private Bitmap buttonBitmap;
    public JoyStickView(Context context) {
        super(context);
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoyStickView(Context context, AttributeSet attrs, int defaultStyle) {
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
        button.setColor(Color.RED);
        button.setStyle(Paint.Style.FILL);
        temp = new RectF();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_1);
        buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_controller_blue);
    }


    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        xPosition = (int) getWidth() / 2;
        yPosition = (int) getWidth() / 2;
        int d = Math.min(xNew, yNew);
        mWidth = d;
        buttonRadius = (int) (d / 2 * 0.5);
        joystickRadius = (int) (d / 2 * 0.25);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and
        // height
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

        setMeasuredDimension(d, d);

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
        centerY = (getHeight()) / 2;

        canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,
                mainCircle);
        temp.set(0,0,mWidth,mWidth);
        canvas.drawBitmap(padBGBitmap, null, temp, paint);

        temp.set(xPosition - buttonRadius, yPosition - buttonRadius, xPosition + buttonRadius, yPosition + buttonRadius);
        canvas.drawBitmap(buttonBitmap, null, temp, paint);

//        canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
    }

    public void setViewEnable(boolean isEnable){
        if(isEnable){
            buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_controller_blue);
        }else {
            buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_controller_blue);
        }
        setEnabled(isEnable);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPosition = (int) event.getX();
        yPosition = (int) event.getY();
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
            if(onJoystickTouchListener != null){
                onJoystickTouchListener.onUp();
            }
            if (onJoystickMoveListener != null){
                xPosition = (int) centerX;
                yPosition = (int) centerY;
                thread.interrupt();
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_1);
                invalidate();
                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(onJoystickTouchListener != null) {
                onJoystickTouchListener.onDown();
            }
            if (onJoystickMoveListener != null){
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
                thread = new Thread(this);
                thread.start();
                onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
            }
        }
        return true;
    }

    private int getAngle() {
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
                    if (onJoystickMoveListener != null){
                        changeBackagroud(getDirection());
                        onJoystickMoveListener.onValueChanged(getAngle(),
                                getPower(), getDirection());
                    }
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
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_2);
                break;
            case FRONT_RIGHT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_9);
                break;
            case LEFT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_3);
                break;
            case LEFT_FRONT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_6);
                break;
            case BOTTOM:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_5);
                break;
            case BOTTOM_LEFT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_7);
                break;
            case RIGHT_BOTTOM:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_8);
                break;
            case RIGHT:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_4);
                break;
            default:
                padBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rocker_bg_in_1);
        }
    }
}
