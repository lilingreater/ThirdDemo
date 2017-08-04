package com.lilin.thirdsdk.thirddemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lilin on 2017/5/3.
 * func :
 */
public class CusNumProgressBar extends View {
    private final Paint paint;

    private TypedArray typedArray;
    /**
     * 进度条长度
     */
    private float mWidth;
    /**
     * 进度条高度
     */
    private float mHeight;
    /**
     * 底色
     */
    private int defaultColor;
    /**
     * 进度颜色
     */
    private int focusColor;

    /**
     * 字体颜色
     */
    private int textColor;
    /**
     * 字体大小
     */
    private float textSize;

    /**
     * 进度最大值
     */
    private int max;

    /**
     * 现在进度
     */
    private int progress;
    /**
     * 目前设置的字
     */
    private String mCurrentDrawText;
    /**
     * 走过的进度
     */
    private Paint mReachedBarPaint;
    /**
     * 为走过的进度
     */
    private Paint mUnreachedBarPaint;
    /**
     * 字
     */
    private Paint mTextPaint;
    private float mDrawTextWidth;
    /**
     * Unreached bar area to draw rect.
     */
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    /**
     * Reached bar area rect.
     */
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);
    /**
     * 字起始位置
     */
    private float mDrawTextStart;
    private int mDrawTextEnd;
    private boolean mDrawReachedBar;
    private boolean mDrawUnreachedBar;

    public Paint getPaint() {
        return paint;
    }

    public TypedArray getTypedArray() {
        return typedArray;
    }

    public void setTypedArray(TypedArray typedArray) {
        this.typedArray = typedArray;
    }

    public float getMWidth() {
        return mWidth;
    }

    public void setMWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    public float getMHeight() {
        return mHeight;
    }

    public void setmHeight(float mHeight) {
        this.mHeight = mHeight;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getFocusColor() {
        return focusColor;
    }

    public void setFocusColor(int focusColor) {
        this.focusColor = focusColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public CusNumProgressBar(Context context) {
        this(context, null);
    }

    public CusNumProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusNumProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumProgressBar);

        //获取自定义属性和默认值
        defaultColor = typedArray.getColor(R.styleable.NumProgressBar_defaultColor, Color.GRAY);
        focusColor = typedArray.getColor(R.styleable.NumProgressBar_focusColor, Color.GREEN);
        mHeight = typedArray.getDimension(R.styleable.NumProgressBar_height, 10);
        mWidth = typedArray.getDimension(R.styleable.NumProgressBar_width, 500);
        textColor = typedArray.getColor(R.styleable.CusCircleProgressBar_textColor, Color.GREEN);
        textSize = typedArray.getDimension(R.styleable.CusCircleProgressBar_textSize, 15);
        max = typedArray.getInteger(R.styleable.CusCircleProgressBar_max, 100);
        typedArray.recycle();
        initializePainters();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDrawRectF();
        if (mDrawReachedBar) {
            canvas.drawRect(mReachedRectF, mReachedBarPaint);
        }
        if (mDrawUnreachedBar) {
            canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
        }
        canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);

    }

    private void calculateDrawRectF() {

        mCurrentDrawText = String.format("%d", progress * 100 / getMax());
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);
        if (getProgress() == 0) {
            mDrawReachedBar = false;
            mDrawTextStart = getPaddingLeft();
        } else {
            mDrawReachedBar = true;
            mReachedRectF.left = getPaddingLeft();
            mReachedRectF.top = getHeight() / 2.0f - mHeight / 2.0f - 40f;
            mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mHeight / 2.0f - 40f;
            mDrawTextStart = mReachedRectF.right;
        }
        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - mDrawTextWidth;
            mReachedRectF.right = mDrawTextStart;
        }

        float unreachedBarStart = mDrawTextStart;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            mDrawUnreachedBar = false;
        } else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unreachedBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mHeight / 2.0f - 40f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mHeight / 2.0f - 40f;
        }

    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(defaultColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(focusColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
    }
}
