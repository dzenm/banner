package com.dzenm.banner;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dzenm.banner.impl.OnRenderItemViewListener;

import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-21 18:06
 */
class ViewPagerAdapter extends PagerAdapter {

    private OnRenderItemViewListener mOnRenderItemViewListener;
    private SparseArray<View> mViewCache;
    private List<View> mViews;

    ViewPagerAdapter(List<View> views) {
        mViews = views;
        mViewCache = new SparseArray<>();
    }

    void setOnRenderItemViewListener(OnRenderItemViewListener onRenderItemViewListener) {
        mOnRenderItemViewListener = onRenderItemViewListener;
    }

    @Override
    public int getCount() {
        return mViews == null ? 0 : mViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * @param container
     * @param position
     * @return 加载一个View视图
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mViewCache.get(position);
        if (view == null) {
            view = mViews.get(position);
            mViewCache.put(position, view);
        }
        ViewParent viewParent = view.getParent();
        if (viewParent != null) {
            ViewGroup parent = (ViewGroup) viewParent;
            parent.removeView(view);
        }
        if (mOnRenderItemViewListener != null) {
            mOnRenderItemViewListener.onRenderItemView(view, position);
        }
        container.addView(view);
        return view;
    }

    /**
     * 移除一个View视图
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
