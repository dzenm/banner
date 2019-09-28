package com.dzenm.banner.impl;

/**
 * @author dzenm
 * @date 2019-09-07 09:19
 */
public interface OnPageSelectedListener {

    /**
     * 页面滑动时, 当滑动页面结束后选中的当前页
     *
     * @param position 当前选中的位置
     */
    void onPageSelected(int position);
}
