package com.github.kizo13.rangebar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class RangeBar extends View {

    private static final String TAG = "RangeBar";

    // State
    private String indicatorName;
    private float mMin = 0f, mMax = 100f, mValue = 50f;

    // Color
    protected int colorNegative = Color.parseColor("#DA4F49");
    protected int colorPositive = Color.parseColor("#5BB75B");
    protected int colorBackground = Color.parseColor("#EDEDED");
    protected int colorLine = Color.parseColor("#FFFFFF");
    protected int colorText = Color.BLACK;

    // Layout
    private int mWidth = 500, mHeight = 100;
    private int mOuterPadding = 5, mInnerPadding = 0;
    private float mBorderRadius = 0f;
    private int mFontSize = 16;

    // Paint
    private Paint backgroundPaint, negativePaint, positivePaint, strokePaint;

    public RangeBar(Context context) {
        super(context);
        init();
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Overrides from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangeBar, 0, 0);

        try {
            indicatorName = a.getString(R.styleable.RangeBar_indicatorName);
            colorNegative = a.getColor(R.styleable.RangeBar_colorNegative, colorNegative);
            colorPositive = a.getColor(R.styleable.RangeBar_colorPositive, colorPositive);
            colorBackground = a.getColor(R.styleable.RangeBar_colorBackground, colorBackground);
            colorText = a.getColor(R.styleable.RangeBar_colorBackground, colorText);
            colorLine = a.getColor(R.styleable.RangeBar_colorLine, colorLine);
            mMin = a.getFloat(R.styleable.RangeBar_minimum, mMin);
            mMax = a.getFloat(R.styleable.RangeBar_maximum, mMax);
            mOuterPadding = a.getInteger(R.styleable.RangeBar_outerPadding, mOuterPadding);
            mInnerPadding = a.getInteger(R.styleable.RangeBar_innerPadding, mInnerPadding);
            mBorderRadius = a.getFloat(R.styleable.RangeBar_borderRadius, mBorderRadius);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(colorBackground);

        negativePaint = new Paint();
        negativePaint.setColor(colorNegative);

        positivePaint = new Paint();
        positivePaint.setColor(colorPositive);

        strokePaint = new Paint();
        strokePaint.setColor(colorLine);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(4);
        strokePaint.setTextSize(mFontSize * getResources().getDisplayMetrics().density);

        setPadding(mOuterPadding, mOuterPadding, mOuterPadding, mOuterPadding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if((getLayoutParams().width==WindowManager.LayoutParams.MATCH_PARENT)
                || (getLayoutParams().width==WindowManager.LayoutParams.FILL_PARENT)) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else mWidth = getLayoutParams().width;

        if((getLayoutParams().height==WindowManager.LayoutParams.MATCH_PARENT)
                ||(getLayoutParams().height==WindowManager.LayoutParams.FILL_PARENT)) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else mHeight = getLayoutParams().height;

        setMeasuredDimension(mWidth, mHeight);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Allow to draw outside the View
        ViewGroup parentView = (ViewGroup)RangeBar.this.getParent();
        parentView.setClipChildren(false);
        parentView.setClipToPadding(false);

        // Set coordinates
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = mWidth-getPaddingRight();
        int bottom = mHeight-getPaddingBottom();
        int halfWidth = mWidth/2;

        // Draw background
        canvas.drawRoundRect((float) left, (float) top, (float) right, (float) bottom,
                mBorderRadius, mBorderRadius, backgroundPaint);

        // Draw colored value bar(s)
        if (isInMiddle()) {
            canvas.drawRoundRect(
                    (mWidth/2)-(mWidth/50),
                    top+mInnerPadding,
                    mWidth/2, bottom-mInnerPadding,
                    mBorderRadius, mBorderRadius,
                    negativePaint);

            canvas.drawRoundRect(
                    mWidth/2,
                    top+mInnerPadding,
                    (mWidth/2)+(mWidth/50),
                    bottom-mInnerPadding,
                    mBorderRadius-(mBorderRadius/10), mBorderRadius-(mBorderRadius-10),
                    positivePaint);
        } else {
            float barWidth = (mWidth/(mMax-mMin))*(mValue-mMin);
            float barLeft = (isPositive() ? mWidth/2 : barWidth+(2*mInnerPadding));
            float barRight = (isPositive() ? barWidth-(2*mInnerPadding) : mWidth/2);

            canvas.drawRoundRect(
                    barLeft,
                    top+mInnerPadding,
                    barRight,
                    bottom-mInnerPadding,
                    mBorderRadius-(mBorderRadius/10), mBorderRadius-(mBorderRadius/10),
                    isPositive() ? positivePaint : negativePaint);
        }

        // Draw vertical line in the middle
        canvas.drawLine((float)halfWidth, (float)top, (float)halfWidth, (float)bottom, strokePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //touched = true;
                Toast.makeText(
                        getContext().getApplicationContext(),
                        (indicatorName == null ? "" : indicatorName + "\n") + String.valueOf(getValue()),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    public boolean isInMiddle() {
        return (mValue-mMin) == ((mMax-mMin)/2);
    }

    public boolean isPositive() {
        return (mValue-mMin) > ((mMax-mMin)/2);
    }

    // Simple accessors
    public float getMin() {
        return mMin;
    }
    public void setMin(float min) {
        this.mMin = min;
    }
    public float getMax() {
        return mMax;
    }
    public void setMax(float max) {
        this.mMax = max;
    }
    public float getValue() {
        return mValue;
    }
    public void setValue(float value) {
        this.mValue = value;
    }

    public int getColorNegative() {
        return colorNegative;
    }

    public void setColorNegative(int colorNegative) {
        this.colorNegative = colorNegative;
    }

    public int getColorPositive() {
        return colorPositive;
    }

    public void setColorPositive(int colorPositive) {
        this.colorPositive = colorPositive;
    }

    public int getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
    }

    public int getColorText() {
        return colorText;
    }

    public void setColorText(int colorText) {
        this.colorText = colorText;
    }
}
