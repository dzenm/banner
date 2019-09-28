package com.dzenm.banner2.impl;

import android.widget.ImageView;

/**
 * @author dzenm
 * @date 2019-09-06 14:20
 */
public interface ImageLoader {

    /**
     * @param view          图片加载的View
     * @param imageResource 图片加载的资源
     */
    void onLoader(ImageView view, Object imageResource);
}
