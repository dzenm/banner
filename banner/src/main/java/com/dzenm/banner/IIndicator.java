package com.dzenm.banner;

import android.widget.LinearLayout;

/**
 * @author dzenm
 * @date 2019-08-09 00:23
 */
public interface IIndicator {

    /**
     * 创建一组指示器
     *
     * @param indicatorLayout 包裹指示器的外层布局
     * @param indicatorMargin 指示器的外边距值
     * @param viewCount       指示器的数量
     */
    void createIndicator(LinearLayout indicatorLayout, int indicatorMargin, int viewCount);
}
