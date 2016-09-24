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
    private static final int ANIMATION_STOP = 0;
    private static final int ANIMATION_START = 1;
    private final int minLineLength = dp2px(getContext(), 40);
    private final int maxLineLength = dp2px(getContext(), 120);
    private final float DEFAULT_CANVAS_ANGLE = 60;
    private int mWidth;
    private int mHeight;
    private @ColorInt int[] colors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    private int baseLineLength = minLineLength;
    private float canvasAngle;
    private Paint paint;
    private List<Animator> animatorList = new ArrayList<>();
    private int dynamicLineLength;//可变化的,用来计算Line的真实高度
    private int step;
    private float circleRadius;
    private float circleY;
    private int playState;

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
        initPaint();
        init();
        playState = ANIMATION_STOP;
    }

    private void init() {
        circleRadius = baseLineLength / 5;
        paint.setStrokeWidth(circleRadius * 2);
        dynamicLineLength = baseLineLength;
        step = 0;
        canvasAngle = DEFAULT_CANVAS_ANGLE;
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDynamicLineLength(float scale) {
        if (playState == ANIMATION_START) {
            playState = ANIMATION_STOP;
            clearAnimator();
        }
        baseLineLength = (int) ((maxLineLength - minLineLength) * scale + minLineLength);
        init();
        invalidate();
    }

    private void clearAnimator() {
        for (int i = 0; i < animatorList.size(); i++) {
            Animator animator = animatorList.get(i);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
        animatorList.clear();
    }

    public void start() {
        if (playState == ANIMATION_STOP) {
            playState = ANIMATION_START;
            animatorList.clear();
            init();
            startRotationAndDecreaseLineLength();
        }
    }

    public void stop() {
        if (playState == ANIMATION_START) {
            playState = ANIMATION_STOP;
            clearAnimator();
            init();
            invalidate();
        }
    }

    private void startRotationAndDecreaseLineLength() {
        ValueAnimator lineChangeDegreesAnimator = ValueAnimator.ofFloat(canvasAngle + 0, canvasAngle + 360);
        lineChangeDegreesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                canvasAngle = (float) animation.getAnimatedValue();
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
                if (playState == ANIMATION_START) {
                    step++;
                    startRotationCircle();
                }
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
                if (playState == ANIMATION_START) {
                    step++;
                    startRotationCircleAndScaleLineLength();
                }
            }
        });
        circleChangeDegreesAnimator.start();
        animatorList.add(circleChangeDegreesAnimator);
    }

    private void startRotationCircleAndScaleLineLength() {
        ValueAnimator circleChangeDegreesAnimator = ValueAnimator.ofFloat(canvasAngle + 0, canvasAngle + 180);
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
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (playState == ANIMATION_START) {
                    step++;
                    startIncreaseLineLength();
                }
            }
        });
        animatorList.add(animatorSet);
        animatorSet.start();
    }

    private void startIncreaseLineLength() {
        ValueAnimator lineChangeLengthAnimator = ValueAnimator.ofInt(-(baseLineLength - dp2px(getContext(), 2)), baseLineLength);
        lineChangeLengthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dynamicLineLength = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        lineChangeLengthAnimator.setDuration(DEFAULT_DURATION);
        lineChangeLengthAnimator.setInterpolator(new LinearInterpolator());
        lineChangeLengthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (playState == ANIMATION_START) {
                    step++;
                    startRotationAndDecreaseLineLength();
                }
            }
        });
        lineChangeLengthAnimator.start();
        animatorList.add(lineChangeLengthAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (step % 4) {
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
            case 3:
                for (int i = 0; i < colors.length; i++) {
                    paint.setColor(colors[i]);
                    drawLine(canvas, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 - dynamicLineLength, mWidth / 2 - baseLineLength / 2.2f, mHeight / 2 + baseLineLength, paint, canvasAngle + (90 * i));
                }
                break;
            default:
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
