package com.dzenm.banner2;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class IndicatorView extends View implements ViewPager.OnPageChangeListener {

    private static final String TAG = IndicatorView.class.getSimpleName();

    private static final double FACTOR = 0.55191502449;
    private static final float SPLIT_RADIUS_FACTOR = 1.5f;

    private int mHeightMeasureSpec;
    private Paint mNormalPaint, mSelectedPaint, mTargetPaint;

    /**
     * 正常显示的指示器的大小, 选中的指示器的大小
     */
    private float mNormalRadius, mSelectedRadius;

    /**
     * 正常显示的指示器的颜色, 选中的指示器的颜色
     */
    private int mNormalColor, mSelectedColor;

    /**
     * 两个指示器之间的距离
     */
    private int mPointInterval;

    /**
     * 指示器滑动的类型
     */
    private @IndicatorScroll
    int mIndicatorScrollType;

    /**
     * 指示器的总数量
     */
    private int mTotalCount;
    private List<PointF> mControlPoints;

    private int mCurrentPagePosition, mTargetPagePosition, mWidth, mHeight;
    private Path mArcPath, mSplitArcPath;
    private float mTranslationFactor;
    private boolean isLoop;

    @IntDef({IndicatorScroll.SCALE, IndicatorScroll.SPLIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorScroll {
        int SCALE = 0;
        int SPLIT = 1;
    }

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView);
        mIndicatorScrollType = typedArray.getInt(R.styleable.IndicatorView_indicatorScrollType, 0);
        mNormalRadius = typedArray.getDimension(
                R.styleable.IndicatorView_normalRadius, 8);
        mSelectedRadius = typedArray.getDimension(R.styleable.IndicatorView_selectedRadius,
                mIndicatorScrollType == IndicatorScroll.SCALE ? 8 : 12);
        mPointInterval = (int) typedArray.getDimension(
                R.styleable.IndicatorView_pointInterval, dp2px(12));
        mNormalColor = typedArray.getColor(R.styleable.IndicatorView_normalColor,
                context.getResources().getColor(android.R.color.holo_red_light));
        mSelectedColor = typedArray.getColor(R.styleable.IndicatorView_selectedColor,
                context.getResources().getColor(android.R.color.darker_gray));
        typedArray.recycle();

        initializePaint();
        adjustControlPointPosition();
    }

    /**
     * 初始化画笔
     */
    private void initializePaint() {
        mArcPath = new Path();
        mSplitArcPath = new Path();
        mControlPoints = new ArrayList<>();
        adjustPaint();
        adjustSplitPoint();
    }

    /**
     * 调整画笔
     */
    private void adjustPaint() {
        if (mNormalPaint == null) mNormalPaint = newPaint(mNormalColor);
        if (mSelectedPaint == null) mSelectedPaint = newPaint(mSelectedColor);
        if (mTargetPaint == null) mTargetPaint = newPaint(mSelectedColor);
    }

    /**
     * 调整Split样式的Point
     */
    private void adjustSplitPoint() {
        if (mIndicatorScrollType != IndicatorScroll.SPLIT) return;
        if (mSelectedRadius < mNormalRadius * SPLIT_RADIUS_FACTOR) {
            mSelectedRadius = (int) (mNormalRadius * SPLIT_RADIUS_FACTOR);
        }
    }

    /**
     * 调整控制圆点的位置
     */
    private void adjustControlPointPosition() {
        // 初始化绘制 1/4 圆弧的三阶贝塞尔曲线控制点相对坐标(相对圆心)
        for (int i = 0; i < 8; i++) {
            float x, y;
            if (i == 0) {           // 右下P0
                x = mNormalRadius;
                y = (float) (mNormalRadius * FACTOR);
            } else if (i == 1) {    // 右下P1
                x = (float) (mNormalRadius * FACTOR);
                y = mNormalRadius;
            } else if (i == 2) {    // 左下P2
                x = -(float) (mNormalRadius * FACTOR);
                y = mNormalRadius;
            } else if (i == 3) {    // 左下P3
                x = -mNormalRadius;
                y = (float) (mNormalRadius * FACTOR);
            } else if (i == 4) {    // 左上P4
                x = -mNormalRadius;
                y = -(float) (mNormalRadius * FACTOR);
            } else if (i == 5) {    // 左上P5
                x = -(float) (mNormalRadius * FACTOR);
                y = -mNormalRadius;
            } else if (i == 6) {    // 右上P6
                x = (float) (mNormalRadius * FACTOR);
                y = -mNormalRadius;
            } else {                // 右上P7
                x = mNormalRadius;
                y = -(float) (mNormalRadius * FACTOR);
            }
            mControlPoints.add(new PointF(x, y));
        }
    }

    /**
     * @param type 设置指示器滑动类型
     */
    public void setIndicatorScrollType(@IndicatorScroll int type) {
        adjustIndicatorType(type);
    }

    private void adjustIndicatorType(int type) {
        if (type == mIndicatorScrollType) return;
        mIndicatorScrollType = type;
        adjustPaint();
        adjustSplitPoint();
        postInvalidate();
    }

    public void bindViewPager(ViewPager viewPager) {
        setViewPagerBindIndicator(viewPager);
    }

    private void setViewPagerBindIndicator(ViewPager viewPager) {
        if (viewPager == null) return;
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        viewPager.addOnPageChangeListener(this);
        if (adapter == null) {
            throw new RuntimeException("please set adapter before binding indicator");
        }
        mTotalCount = adapter.getRealTotalCount();
        Log.d(TAG, "total point count is " + mTotalCount);
        isLoop = adapter.getLoop();
        measure(0, mHeightMeasureSpec);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 动态计算当前页与目标页位置
        if (positionOffset > 0) {             // 开始滑动后
            if (position < mCurrentPagePosition) {  // 向左滑动
                mTranslationFactor = 1 - positionOffset;
                mCurrentPagePosition = position + 1;
                mTargetPagePosition = position;
            } else {                                // 向右滑动
                mTranslationFactor = positionOffset;
                mCurrentPagePosition = position;
                mTargetPagePosition = position + 1;
            }
        } else {                                    // 滑动停止时
            mTranslationFactor = positionOffset;
            mCurrentPagePosition = position;
            mTargetPagePosition = position;
        }
        if (isLoop) {
            mCurrentPagePosition = mCurrentPagePosition - 1;
            mTargetPagePosition = mTargetPagePosition - 1;
        }
        postInvalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeightMeasureSpec = heightMeasureSpec;
        mWidth = mTotalCount > 0 ?
                (mTotalCount - 1) * mPointInterval + 2 * (int) mSelectedRadius : 0;

        mHeight = heightMeasureSpec == MeasureSpec.EXACTLY ?
                getDefaultSize(getSuggestedMinimumHeight(), mHeightMeasureSpec) :
                (int) (mSelectedRadius * 2) + 2;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTotalCount <= 0 || (mHeight <= 0 && mWidth <= 0)) return;
        if (mIndicatorScrollType == IndicatorScroll.SPLIT) {
            drawSplitIndicator(canvas);
        } else {
            drawScaleIndicator(canvas);
        }
    }

    /**
     * 绘制指示器
     *
     * @param canvas 绘制的画布
     */
    private void drawScaleIndicator(Canvas canvas) {
        float radius, centerXOffset = mSelectedRadius;
        float centerX, centerY = mHeight / 2;
        for (int position = 0; position < mTotalCount; position++) {
            centerX = position * mPointInterval + centerXOffset;
            // 根据ViewPager滑动动态调整当前选中点和目标点半径
            if (position == mCurrentPagePosition) {
                radius = mNormalRadius + (1 - mTranslationFactor) * (mSelectedRadius - mNormalRadius);
            } else if (position == mTargetPagePosition) {
                radius = mNormalRadius + (mTranslationFactor) * (mSelectedRadius - mNormalRadius);
            } else {
                radius = mNormalRadius;
            }
            mArcPath.reset();
            mArcPath.moveTo(centerX + radius, centerY);

            for (int pointPosition = 0; pointPosition < mControlPoints.size() / 2; pointPosition++) {
                adjustPointScale(position, pointPosition, centerX, centerY, radius);
                setIndicatorScrollAnimator(canvas, position);
            }
        }
    }

    /**
     * 调整缩放的点
     *
     * @param position      ViewPager的位置
     * @param pointPosition 指示器的位置
     * @param x             中心点X
     * @param y             中心点Y
     * @param radius        圆角
     */
    private void adjustPointScale(int position, int pointPosition, float x, float y, float radius) {
        float endX, endY;
        if (pointPosition == 0) {               // 相对于圆心，第一象限
            endX = x;
            endY = y + radius;
        } else if (pointPosition == 1) {        // 相对于圆心，第二象限
            endX = x - radius;
            endY = y;
        } else if (pointPosition == 2) {        // 相对于圆心，第三象限
            endX = x;
            endY = y - radius;
        } else {                    // 相对于圆心，第四象限
            endX = x + radius;
            endY = y;
        }
        float controlPointX1, controlPointY1, controlPointX2, controlPointY2;
        if (position == mCurrentPagePosition || position == mTargetPagePosition) {
            // 控制点坐标根据ViewPager滑动做相应缩放
            float stretchFactor = radius / mNormalRadius;
            controlPointX1 = x + mControlPoints.get(pointPosition * 2).x * stretchFactor;
            controlPointY1 = y + mControlPoints.get(pointPosition * 2).y * stretchFactor;
            controlPointX2 = x + mControlPoints.get(pointPosition * 2 + 1).x * stretchFactor;
            controlPointY2 = y + mControlPoints.get(pointPosition * 2 + 1).y * stretchFactor;
        } else {
            controlPointX1 = x + mControlPoints.get(pointPosition * 2).x;
            controlPointY1 = y + mControlPoints.get(pointPosition * 2).y;
            controlPointX2 = x + mControlPoints.get(pointPosition * 2 + 1).x;
            controlPointY2 = y + mControlPoints.get(pointPosition * 2 + 1).y;
        }
        mArcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, endX, endY);
    }

    /**
     * 绘制指示器滑动的动画
     *
     * @param canvas   绘制使用的画布
     * @param position 绘制的位置
     */
    private void setIndicatorScrollAnimator(Canvas canvas, int position) {
        int alpha = (int) (mTranslationFactor * 255);
        if (position == mCurrentPagePosition) {
            mSelectedPaint.setAlpha(255 - alpha);
            mNormalPaint.setAlpha(alpha);
            canvas.drawPath(mArcPath, mNormalPaint);
            canvas.drawPath(mArcPath, mSelectedPaint);
        } else if (position == mTargetPagePosition) {
            mTargetPaint.setAlpha(alpha);
            mNormalPaint.setAlpha(255 - alpha);
            canvas.drawPath(mArcPath, mNormalPaint);
            canvas.drawPath(mArcPath, mTargetPaint);
        } else {
            mNormalPaint.setAlpha(255);
            canvas.drawPath(mArcPath, mNormalPaint);
        }
    }

    private void drawSplitIndicator(Canvas canvas) {
        float centerX, centerY = mHeight / 2, endX, endY, centerXOffset = mSelectedRadius;
        float selectedSplitEndX = 0, selectedSplitEndY = 0;

        // 控制分裂圆形半径的系数
        float splitRadiusFactor;

        if (mTranslationFactor * mPointInterval <= 2 * mNormalRadius) {
            splitRadiusFactor = mTranslationFactor * mPointInterval / (mNormalRadius * 2);
            splitRadiusFactor = (float) Math.log(1 + (Math.E - 1) * splitRadiusFactor);
        } else if (mTranslationFactor * mPointInterval > mPointInterval - 2 * mNormalRadius) {
            splitRadiusFactor = (mPointInterval - mTranslationFactor * mPointInterval) / (2 * mNormalRadius);
            splitRadiusFactor = (float) Math.log((Math.E - 1) * splitRadiusFactor + 1);
        } else {
            splitRadiusFactor = 1;
        }

        // 动态调整分裂圆形的半径
        float selectedSplitRadius = mNormalRadius + (1 - splitRadiusFactor) * (mSelectedRadius - mNormalRadius);
        // 分裂圆形的滑动偏移量
        float selectedSplitPointCenterXOffset = mCurrentPagePosition < mTargetPagePosition ? mTranslationFactor * (mPointInterval) : -mTranslationFactor * (mPointInterval);

        for (int i = 0; i < mTotalCount; i++) {
            centerX = i * mPointInterval + centerXOffset;
            mArcPath.reset();
            mArcPath.moveTo(centerX + mNormalRadius, centerY);
            mSplitArcPath.reset();

            if (i == mCurrentPagePosition) {
                float splitOffset = getSplitOffset();
                if (mCurrentPagePosition == mTargetPagePosition) {
                    mSplitArcPath.moveTo(centerX + selectedSplitPointCenterXOffset + selectedSplitRadius, centerY);
                } else if (mCurrentPagePosition > mTargetPagePosition) {
                    mSplitArcPath.moveTo(centerX + selectedSplitPointCenterXOffset + selectedSplitRadius + splitOffset, centerY);
                } else {
                    float currentX = centerX + selectedSplitPointCenterXOffset + selectedSplitRadius;
                    // 根据粘合偏移量控制分裂圆形的起点(初始滑动分裂阶段为零，后半段粘合时有效)
                    mSplitArcPath.moveTo(currentX + getCurrentBondingOffset(currentX - centerX), centerY);
                    // 根据滑动分裂偏移量调整当前圆形的起点
                    mArcPath.moveTo(centerX + mNormalRadius + splitOffset, centerY);
                }
            }

            if (i == mTargetPagePosition && mCurrentPagePosition > mTargetPagePosition) {
                mArcPath.moveTo(centerX + mNormalRadius + getTargetBondingOffset(), centerY);
            }

            for (int k = 0; k < mControlPoints.size() / 2; k++) {
                if (k == 0) {
                    endX = centerX;
                    endY = centerY + mNormalRadius;
                    if (i == mCurrentPagePosition) {
                        selectedSplitEndX = centerX + selectedSplitPointCenterXOffset;
                        selectedSplitEndY = centerY + selectedSplitRadius;
                    }
                } else if (k == 1) {
                    endX = centerX - mNormalRadius;
                    endY = centerY;
                    if (i == mCurrentPagePosition) {
                        selectedSplitEndX = centerX + selectedSplitPointCenterXOffset - selectedSplitRadius;
                        selectedSplitEndY = centerY;
                        if (mCurrentPagePosition != mTargetPagePosition) {
                            float offset = getSplitOffset();
                            if (mCurrentPagePosition > mTargetPagePosition) {
                                endX -= offset;
                                selectedSplitEndX -= getCurrentBondingOffset(centerX - selectedSplitEndX);
                            } else {
                                selectedSplitEndX -= offset;
                            }
                        }
                    }
                    if (i == mTargetPagePosition && mCurrentPagePosition < mTargetPagePosition) {
                        endX -= getTargetBondingOffset();
                    }
                } else if (k == 2) {
                    endX = centerX;
                    endY = centerY - mNormalRadius;
                    if (i == mCurrentPagePosition) {
                        selectedSplitEndX = centerX + selectedSplitPointCenterXOffset;
                        selectedSplitEndY = centerY - selectedSplitRadius;
                    }
                } else {
                    endX = centerX + mNormalRadius;
                    endY = centerY;
                    if (i == mCurrentPagePosition) {
                        selectedSplitEndX = centerX + selectedSplitPointCenterXOffset + selectedSplitRadius;
                        selectedSplitEndY = centerY;
                        if (mCurrentPagePosition != mTargetPagePosition) {
                            float offset = getSplitOffset();
                            if (mCurrentPagePosition < mTargetPagePosition) {
                                endX += offset;
                                selectedSplitEndX += getCurrentBondingOffset(selectedSplitEndX - centerX);
                            } else {
                                selectedSplitEndX += offset;
                            }
                        }
                    }
                    if (i == mTargetPagePosition && mCurrentPagePosition > mTargetPagePosition) {
                        endX += getTargetBondingOffset();
                    }
                }
                float controlPointX1 = centerX + mControlPoints.get(k * 2).x;
                float controlPointY1 = centerY + mControlPoints.get(k * 2).y;
                float controlPointX2 = centerX + mControlPoints.get(k * 2 + 1).x;
                float controlPointY2 = centerY + mControlPoints.get(k * 2 + 1).y;
                mArcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, endX, endY);
                canvas.drawPath(mArcPath, mNormalPaint);

                if (i == mCurrentPagePosition) {
                    float stretchFactor = selectedSplitRadius / mNormalRadius;
                    controlPointX1 = centerX + selectedSplitPointCenterXOffset + mControlPoints.get(k * 2).x * stretchFactor;
                    controlPointY1 = centerY + mControlPoints.get(k * 2).y * stretchFactor;
                    controlPointX2 = centerX + selectedSplitPointCenterXOffset + mControlPoints.get(k * 2 + 1).x * stretchFactor;
                    controlPointY2 = centerY + mControlPoints.get(k * 2 + 1).y * stretchFactor;
                    mSplitArcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, selectedSplitEndX, selectedSplitEndY);
                    canvas.drawPath(mSplitArcPath, mNormalPaint);
                }
            }
        }
    }

    private float getSplitOffset() {
        float participantX = mTranslationFactor * mPointInterval;
        if (participantX > SPLIT_RADIUS_FACTOR * mNormalRadius * 2) {
            participantX = 0;
        }
        float offsetFactor = SPLIT_RADIUS_FACTOR - participantX / (2 * mNormalRadius);
        offsetFactor = offsetFactor > 2 * (SPLIT_RADIUS_FACTOR - 1) ? 0 : offsetFactor;
        return offsetFactor * participantX;
    }

    private float getTargetBondingOffset() {
        float participantX = mTranslationFactor * mPointInterval - (mPointInterval - SPLIT_RADIUS_FACTOR * mNormalRadius * 2);
        if (participantX < 0) return 0;
        return (SPLIT_RADIUS_FACTOR - participantX / (2 * mNormalRadius)) * participantX;
    }

    private float getCurrentBondingOffset(float currentOffsetX) {
        float participantX = mTranslationFactor * mPointInterval - (mPointInterval - SPLIT_RADIUS_FACTOR * mNormalRadius * 2);
        if (participantX < 0) return 0;
        float offset = (SPLIT_RADIUS_FACTOR - participantX / (2 * mNormalRadius)) * participantX;
        if (offset + currentOffsetX > mPointInterval + mNormalRadius) {
            offset -= offset + currentOffsetX - mPointInterval - mNormalRadius;
        }
        if (offset < 0) offset = 0;
        return offset;
    }

    /**
     * @param color 颜色值
     * @return 创建一个Point Paint
     */
    private Paint newPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    private static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                Resources.getSystem().getDisplayMetrics());
    }
}
