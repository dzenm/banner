package com.dzenm.banner.impl;

import android.view.View;

/**
 * @author dzenm
 * @date 2019-09-06 14:20
 */
public interface ImageLoader {

    /**
     * @param view          图片加载的View
     * @param imageResource 图片加载的资源
     */
    void onLoader(View view, Object imageResource);
}
