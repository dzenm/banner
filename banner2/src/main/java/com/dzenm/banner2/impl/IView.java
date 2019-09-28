package com.dzenm.banner2.impl;

import android.view.View;

/**
 * @author dzenm
 * @date 2019-09-09 14:37
 */
public interface IView {

    /**
     * @param object   view加载的资源
     * @param position 创建的位置
     * @return 创建的item view
     */
    View createItemView(Object object, int position);
}
