package com.xingstarx.canvas.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiongxingxing on 16/9/22.
 */

public class CustomView extends View {
    private int mWidth;
    private int mHeight;
    private
    @ColorInt
    int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private int baseLine = dp2px(getContext(), 80);
    private float defaultDegrees = 60;
    private Paint paint;

    public CustomView(Context context) {
        super(context);
        initView();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5 * 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < colors.length; i++) {
            paint.setColor(colors[i]);
            drawLine(canvas, mWidth / 2 - baseLine / 2.2f, mHeight / 2 - baseLine, mWidth / 2 - baseLine / 2.2f, mHeight / 2 + baseLine, paint, defaultDegrees + (90 * i));
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY, @NonNull Paint paint, float degrees) {
        canvas.rotate(degrees, mWidth / 2, mHeight / 2);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.rotate(-degrees, mWidth / 2, mHeight / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

}
