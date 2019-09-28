package com.dzenm.banner2;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.banner2.impl.IView;
import com.dzenm.banner2.impl.OnPageSelectedListener;
import com.dzenm.banner2.impl.OnRenderItemViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class ViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private ViewPager mViewPager;

    /**
     * 缓存已创建的页面, 防止重复创建
     */
    private SparseArray<View> mViewCache;

    /**
     * 创建的view
     */
    private ArrayList<View> mViews;

    /**
     * 是否循环显示
     */
    private boolean isLoop;

    /**
     * View的实际数量
     */
    private int mRealTotalCount = -1;

    /**
     * 自定义创建view的接口 {@link IView}
     */
    private IView mIView;

    private OnRenderItemViewListener mOnRenderItemViewListener;

    private OnPageSelectedListener mOnPageSelectedListener;

    /**
     * 当前显示的实际页面
     */
    protected int mCurrentRealPosition = 0;

    public ViewPagerAdapter(@NonNull List data, ViewPager viewPager, IView iView, boolean loop) {
        mViewPager = viewPager;
        mIView = iView;
        isLoop = loop;

        mViews = new ArrayList<>();
        mViewCache = new SparseArray<>();
        if (mRealTotalCount == -1) mRealTotalCount = data.size();
        Log.d(TAG, "real total count is " + mRealTotalCount);
        initialize(data);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialize(@NonNull List data) {
        if (isLoop && data.size() > 1) {
            data.add(0, data.get(data.size() - 1));     // 添加最后一页到第一页
            data.add(data.get(1));      // 添加第一页(经过上行的添加已经是第二页了)到最后一页
        }

        int position = 0;
        for (Object object : data) {
            mViews.add(mIView.createItemView(object, position));
            position++;
        }
        Log.d(TAG, "data size is " + data.size());
        mViewPager.setAdapter(this);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(data.size());
        mViewPager.setCurrentItem(isLoop ? 1 : 0, false);
    }

    public void setOnRenderItemViewListener(OnRenderItemViewListener onRenderItemViewListener) {
        mOnRenderItemViewListener = onRenderItemViewListener;
    }

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
    }

    public void setIView(IView iView) {
        mIView = iView;
    }

    public int getRealTotalCount() {
        return mRealTotalCount;
    }

    public int getCurrentRealPosition() {
        return mCurrentRealPosition;
    }

    public void setCurrentRealPosition(int currentRealPosition) {
        mCurrentRealPosition = currentRealPosition;
    }

    public void nextPage(boolean smoothScroll) {
        mViewPager.setCurrentItem(mCurrentRealPosition++, smoothScroll);
    }

    public void lastPage(boolean smoothScroll) {
        mViewPager.setCurrentItem(mCurrentRealPosition--, smoothScroll);
    }

    public boolean getLoop() {
        return isLoop;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mViewCache.get(position);
        if (view == null) {
            view = mViews.get(position);
            mViewCache.put(position, view);
        }
        if (mOnRenderItemViewListener != null) {
            mOnRenderItemViewListener.onRenderItemView(view, position);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViews.get(position));
    }

    /**
     * @param position             静止时, 显示当前页所在的位置, 向左滑动时, position立刻切换为上一页
     *                             向右滑动时，需要等滑动结束后, 才会切换下一页所在的位置
     * @param positionOffset       静止时为0.0, 从左往右滑动的变化[1, 0], 从右往左滑动的变化[0, 1]
     * @param positionOffsetPixels 静止时为0, 从左往右滑动的变化[1000, 0], 从右往左滑动的变化[0, 1000]
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageSelectedListener != null) mOnPageSelectedListener.onPageSelected(position);
        mCurrentRealPosition = position;
    }

    /**
     * 当用手指滑动时，在手指滑动的时刻触发state==1
     * 滑动停止时，先调用state==2，在调用state==0
     * <p>
     * 当不用手指滑动时，滑动的时刻不会调用state==1
     * 直接等滑动结束时，先调用state==2，在调用state==0
     *
     * @param state 1.正在滑动 2.滑动结束
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        // 若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE) return;
        if (isLoop) adjustCurrentRealPosition();
    }

    /**
     * 重新调整页面
     */
    private void adjustCurrentRealPosition() {
        Log.d(TAG, "current position is " + mCurrentRealPosition);
        if (mCurrentRealPosition == 0) {
            // 若当前为第一张，设置页面为倒数第二张
            mViewPager.setCurrentItem(mViews.size() - 2, false);
        } else if (mCurrentRealPosition == mViews.size() - 1) {
            // 若当前为倒数第一张，设置页面为第二张
            mViewPager.setCurrentItem(1, false);
        }
    }
}
