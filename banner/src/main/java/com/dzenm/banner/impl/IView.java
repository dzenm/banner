package com.dzenm.banner.impl;

import android.view.View;

import java.util.List;

/**
 * @author dzenm
 * @date 2019-08-09 00:23
 */
public interface IView {

    /**
     * 创建一组View
     *
     * @param views     保存view的List
     * @param isLoop    是否循环显示
     * @param viewCount view的数量
     */
    void createView(List<View> views, boolean isLoop, int viewCount);

}
