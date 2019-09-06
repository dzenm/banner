package com.dzenm.banner;

import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * @author dzenm
 * @date 2019-08-08 16:32
 */
class PagerHelper implements ViewPager.OnPageChangeListener, ViewTreeObserver.OnGlobalLayoutListener {

    final static int LEFT_PAGE = 0;                 // 左边显示页（根据index来调整左边页应该显示的图片）
    private final static int CENTER_PAGE = 1;       // 中间显示页（永远停留在本页）
    final static int RIGHT_PAGE = 2;                // 右边显示页（根据index来调整右边页应该显示的图片）
    final static int COUNT_PAGE = 3;                // 创建页面对象的个数

    private ViewPager mViewPager;
    private LinearLayout mIndicatorLayout;
    private ImageView mIndicatorImageView;

    /**
     * 是否循环显示
     */
    private boolean isLoop;

    /**
     * 是否显示指示灯
     */
    private boolean isShowIndicator;

    /**
     * 两个指示灯的间距
     */
    private float mIndicatorDistance;

    /**
     * View的数量
     */
    private int mViewCount;

    /**
     * 当前显示的View真正的位置
     */
    private int mCurrentViewPosition;

    private OnViewPagerChangeListener mOnViewPagerChangeListener;

    private PagerLayout.OnPageSelectedListener mOnPageSelectedListener;

    void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);                                  // 设置ViewPager适配器
        mViewPager.setOffscreenPageLimit(COUNT_PAGE);
        mViewPager.setCurrentItem(isLoop ? CENTER_PAGE : LEFT_PAGE);
        mViewPager.addOnPageChangeListener(this);                        // 监听ViewPager滑动
    }

    void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    void setIndicatorLayout(LinearLayout indicatorLayout) {
        mIndicatorLayout = indicatorLayout;
    }

    void setIndicatorImageView(ImageView ivIndicator) {
        mIndicatorImageView = ivIndicator;
        // 监听小圆点滑动的跳转
        ivIndicator.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    void setLoop(boolean loop) {
        isLoop = loop;
    }

    void setShowIndicator(boolean showIndicator) {
        isShowIndicator = showIndicator;
    }

    void setViewCount(int viewCount) {
        mViewCount = viewCount;
    }

    int getCurrentViewPosition() {
        return mCurrentViewPosition;
    }

    void setOnViewPagerChangeListener(OnViewPagerChangeListener onViewPagerChangeListener) {
        mOnViewPagerChangeListener = onViewPagerChangeListener;
    }

    void setOnPageSelectedListener(PagerLayout.OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
    }

    /**
     * 小圆点滑动监听事件
     */
    @Override
    public void onGlobalLayout() {
        mIndicatorDistance = mIndicatorLayout.getChildAt(1).getLeft() - mIndicatorLayout.getChildAt(0).getLeft();    // 两个圆点之间的距离
        mIndicatorImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0) {  // positionOffset等于0时处于静止, 静止时调整页面
            if (isLoop) {
                if (position == CENTER_PAGE) return;
                setLoopViewPosition(position);
                setLoopImagePosition();
            } else {
                mCurrentViewPosition = position;
            }
        } else {                    // 在滑动时监听滑动的偏移量
            if (isShowIndicator) {  // 提示的小圆点
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIndicatorImageView.getLayoutParams();
                if (isLoop) {
                    float offset = indicatorBehavior(position, positionOffset, mCurrentViewPosition, mViewCount);
                    params.leftMargin = (int) (offset * mIndicatorDistance);
                } else {
                    params.leftMargin = (int) ((positionOffset + position) * mIndicatorDistance);
                }
                mIndicatorImageView.setLayoutParams(params);
            }
        }
    }

    /**
     * 指示器的移动的行为
     *
     * @param position        指示器的位置
     * @param offset          偏移量
     * @param currentPosition 当前实际所在的位置
     * @param size            总的View的个数
     * @return 指示器的实际运动量
     */
    private float indicatorBehavior(int position, float offset, int currentPosition, int size) {
        float offsetDistance = 0;
        if (position == 0) {                    // 左滑(offset从1.0-0.0结束)
            if (currentPosition == 0) {         // 是否是第一个向左滑动，并且显示到最后一个
                if (offset > 0.5) {
                    offsetDistance = offset - 1;
                } else {
                    offsetDistance = (size - 1) + offset;
                }
            } else {
                offsetDistance = currentPosition - (1 - offset);
            }
        } else if (position == 1) {             // 右滑(offset从0.0-1.0结束)
            if (currentPosition == size - 1) {  // 是否是最后一个向左滑动，并且显示到第一个
                if (offset < 0.5) {
                    offsetDistance = (size - 1) + offset;
                } else {
                    offsetDistance = offset - 1;
                }
            } else {
                offsetDistance = currentPosition + offset;
            }
        }
        return offsetDistance;
    }

    /**
     * 当循环的时候在每次滑动时改变currentPage
     */
    private void setLoopViewPosition(int position) {
        // 初始 mCurrentImagePosition 为0, position为1, 由于滑动时进入直接判断
        mCurrentViewPosition = position > CENTER_PAGE ? mCurrentViewPosition + 1 : mCurrentViewPosition - 1;
        // 该if语句在于判断循环时, 在 mCurrentImagePosition == 0 时左滑 mCurrentImagePosition == -1
        // 表示 mCurrentImagePosition 为最后一页, 这用于重置页面位置, 同理, mCurrentImagePosition == mSize - 1也是如此
        if (mCurrentViewPosition == -1) {              // 起始页左滑，将左边那页设置为最后一页
            mCurrentViewPosition = mViewCount - 1;     // 最后一页
        } else if (mCurrentViewPosition == mViewCount) {   // 最后一页右滑，将右边那页设置为起始页
            mCurrentViewPosition = 0;                      // 第一页
        }
    }

    /**
     * 当循环的时候在每次滑动之后对图片重新调整
     */
    private void setLoopImagePosition() {
        /*
         * 设置左页的数据
         * 判断当前位置是否为数据起始位置，如果是（即0）将左页的数据设置为最后一个数据
         */
        if (mCurrentViewPosition == 0) {
            mOnViewPagerChangeListener.onViewPagerChange(LEFT_PAGE, mViewCount - 1);
        } else {
            mOnViewPagerChangeListener.onViewPagerChange(LEFT_PAGE, mCurrentViewPosition - 1);
        }

        /*
         * 设置中间页的数据
         */
        mOnViewPagerChangeListener.onViewPagerChange(CENTER_PAGE, mCurrentViewPosition);

        /*
         * 设置右页的数据
         * 判断当前位置是否为数据末尾，如果是（即size-1）将右边的数据设置为第一个数据
         */
        if (mCurrentViewPosition == mViewCount - 1) {
            mOnViewPagerChangeListener.onViewPagerChange(RIGHT_PAGE, 0);
        } else {
            mOnViewPagerChangeListener.onViewPagerChange(RIGHT_PAGE, mCurrentViewPosition + 1);
        }
        /*
         * 滑动结束后将当前页设置为第二页
         * 即ViewPager的当前显示position，本来为1，每次滑动之后会变为2，需要手动将它设置为1
         */
        mViewPager.setCurrentItem(CENTER_PAGE, false);
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageSelectedListener != null) {
            mOnPageSelectedListener.onPageSelected(position);
        }
    }

    /**
     * 当用手指滑动时，在手指滑动的时刻触发state==1
     * 滑动停止时，先调用state==2，在调用state==0
     * <p>
     * 当不用手指滑动时，滑动的时刻不会调用state==1
     * 直接等滑动结束时，先调用state==2，在调用state==0
     *
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    interface OnViewPagerChangeListener {
        void onViewPagerChange(int viewPosition, int position);
    }
}
