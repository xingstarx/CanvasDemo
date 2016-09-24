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
    private static final int DEFAULT_DURATION = 2000;
    private final int minLineLength = dp2px(getContext(), 40);
    private final int maxLineLength = dp2px(getContext(), 120);
    private int mWidth;
    private int mHeight;
    private
    @ColorInt
    int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private int baseLineLength = minLineLength;
    private final float DEFAULT_CANVAS_ANGLE = 0;
    private float canvasAngle;
    private Paint paint;
    private List<Animator> animatorList = new ArrayList<>();
    private int dynamicLineLength;//可变化的,用来计算Line的真实高度
    private int step;
    private float circleRadius;
    private float circleY;

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
        circleRadius = baseLineLength / 5;
        paint.setStrokeWidth(circleRadius * 2);
        dynamicLineLength = baseLineLength;
        step = 0;
        canvasAngle = DEFAULT_CANVAS_ANGLE;
    }

    public void setDynamicLineLength(float scale) {
        clearAnimator();
        baseLineLength = (int) ((maxLineLength - minLineLength) * scale + minLineLength);
        initView();
        invalidate();
    }

    private void clearAnimator() {
        for (int i = 0; i< animatorList.size(); i++) {
            Animator animator = animatorList.get(i);
            if (animator != null && animator.isRunning()) {
                animatorList.get(i).cancel();
            }
        }
        animatorList.clear();
    }

    public void start() {
        clearAnimator();
        ValueAnimator lineChangeDegreesAnimator = ValueAnimator.ofFloat(canvasAngle + 0, canvasAngle + 360);
        lineChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                canvasAngle = (float) animation.getAnimatedValue();
                Log.e(TAG, "onAnimationUpdate canvasAngle == " + canvasAngle);
            }
        });

        ValueAnimator lineChangeLengthAnimator = ValueAnimator.ofInt(baseLineLength, -baseLineLength);
        lineChangeLengthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dynamicLineLength = (int) animation.getAnimatedValue();
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
        ValueAnimator circleChangeDegreesAnimator = ValueAnimator.ofFloat(canvasAngle + 0, canvasAngle + 180);
        circleChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                canvasAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        circleChangeDegreesAnimator.setDuration(DEFAULT_DURATION);
        circleChangeDegreesAnimator.setInterpolator(new LinearInterpolator());
        circleChangeDegreesAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                step++;
                startRotationCircleAndScaleLineLength();
            }
        });
        circleChangeDegreesAnimator.start();
        animatorList.add(circleChangeDegreesAnimator);
    }

    private void startRotationCircleAndScaleLineLength() {
        ValueAnimator circleChangeDegreesAnimator = ValueAnimator.ofFloat(canvasAngle + 0, canvasAngle + 90, canvasAngle + 180);
        circleChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                canvasAngle = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator lineChangeLengthAnimator = ValueAnimator.ofFloat(baseLineLength, baseLineLength / 4f, baseLineLength);
        lineChangeLengthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleY = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(DEFAULT_DURATION);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(circleChangeDegreesAnimator, lineChangeLengthAnimator);
        animatorList.add(animatorSet);
        animatorSet.start();
    }


    public void stop() {
        clearAnimator();
        initView();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (step) {
            case 0:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawLine(canvas, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 - dynamicLineLength, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 + baseLineLength, paint, canvasAngle + (90 * i));
                }
                break;
            case 1:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawCircle(canvas, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 + baseLineLength, circleRadius, paint, canvasAngle + (90 * i));
                }
                break;
            case 2:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawCircle(canvas, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 + circleY, circleRadius, paint, canvasAngle + (90 * i));
                }
                break;
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY, @NonNull Paint paint, float degrees) {
        canvas.rotate(degrees, mWidth / 2, mHeight / 2);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.rotate(-degrees, mWidth / 2, mHeight / 2);
    }

    private void drawCircle(Canvas canvas, float cx, float cy, float radius, @NonNull Paint paint, float degrees) {
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
