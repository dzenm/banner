package com.dzenm.banner2.impl;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * @author dzenm
 * @date 2019-09-07 09:22
 */
public interface PageTransformer {

    /**
     * 页面跳转时, 设置页面跳转的动画
     *
     * @param page      当前view根据position的位置
     * @param viewPager ViewPager
     * @param position  页面移动的偏移量
     */
    void transformPage(@NonNull View page, @NonNull ViewPager viewPager, float position);
}
