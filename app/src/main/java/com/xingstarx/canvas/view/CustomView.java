package com.xingstarx.canvas.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongxingxing on 16/9/22.
 */

public class CustomView extends View {
    private static final String TAG = "CustomView";
    private int mWidth;
    private int mHeight;
    private
    @ColorInt
    int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private int baseLine = dp2px(getContext(), 80);
    private float defaultDegrees = 60;
    private Paint paint;
    private List<Animator> animatorList = new ArrayList<>();
    private static final int DEFAULT_DURATION = 2000;
    private int lineLength;
    private int step = 0;
    private float radius = 5 * 8;

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
        paint.setStrokeWidth(radius);

        lineLength = baseLine;
    }

    public void start() {
        ValueAnimator lineChangeDegreesAnimator = ValueAnimator.ofFloat(defaultDegrees + 0, defaultDegrees + 360);
        lineChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                defaultDegrees = (float) animation.getAnimatedValue();
                Log.e(TAG, "onAnimationUpdate defaultDegrees == " + defaultDegrees);
            }
        });

        ValueAnimator lineChangeLengthAnimator = ValueAnimator.ofInt(baseLine, -baseLine);
        lineChangeLengthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineLength = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(lineChangeDegreesAnimator, lineChangeLengthAnimator);
        animatorSet.setDuration(DEFAULT_DURATION);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                step++;
                startRotationCircle();
            }
        });
        animatorSet.start();
        animatorList.add(animatorSet);
    }


    private void startRotationCircle() {
        ValueAnimator circleChangeDegreesAnimator = ValueAnimator.ofFloat(defaultDegrees + 0, defaultDegrees + 180);
        circleChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                defaultDegrees = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        circleChangeDegreesAnimator.setDuration(DEFAULT_DURATION);
        circleChangeDegreesAnimator.setInterpolator(new LinearInterpolator());
        circleChangeDegreesAnimator.start();
        animatorList.add(circleChangeDegreesAnimator);
    }



    public void stop() {
        for(int i = 0; i < animatorList.size(); i++) {
            Animator animator = animatorList.get(i);
            animator.cancel();
        }
        animatorList.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (step) {
            case 0:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawLine(canvas, mWidth / 2 - baseLine / 2.2f, mHeight / 2 - lineLength, mWidth / 2 - baseLine / 2.2f, mHeight / 2 + baseLine, paint, defaultDegrees + (90 * i));
                }
                break;
            case 1:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawCircle(canvas, mWidth / 2 - baseLine / 2.2f, mHeight / 2 + baseLine, radius, paint, defaultDegrees + (90 * i));
                }
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY, @NonNull Paint paint, float degrees) {
        canvas.rotate(degrees, mWidth / 2, mHeight / 2);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.rotate(-degrees, mWidth / 2, mHeight / 2);
    }

    private void drawCircle(Canvas canvas, float cx, float cy, float radius,@NonNull Paint paint, float degrees) {
        canvas.rotate(degrees, mWidth / 2, mHeight / 2);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.rotate(-degrees, mWidth / 2, mHeight / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

}
