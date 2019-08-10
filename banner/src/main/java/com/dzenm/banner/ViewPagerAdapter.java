package com.dzenm.banner;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-21 18:06
 */
class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViews;

    ViewPagerAdapter(List<View> views) {
        mViews = views;
    }

    /**
     * 图片的数量
     *
     * @return
     */
    @Override
    public int getCount() {
        return mViews == null ? 0 : mViews.size();
    }

    /**
     * 判断是否是当前View视图
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * 加载一个View视图
     *
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mViews.get(position);
        ViewParent viewParent = view.getParent();
        if (viewParent != null) {
            ViewGroup parent = (ViewGroup) viewParent;
            parent.removeView(view);
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
