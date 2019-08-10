package com.dzenm.banner;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * @author dzenm
 * @date 2019-08-10 21:00
 */
class PagerLayoutTransformer implements ViewPager.PageTransformer {

    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.75f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setAlpha(MIN_ALPHA);
        } else if (position < 1) {
            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            // [0, 1 ） 相对于当前选中页，其右边第一页
            if (position > 0) page.setTranslationX(-scaleFactor);
            // [-1, 0 ) 相对于当前选中页，其左边的第一页
            else if (position < 0) page.setTranslationX(scaleFactor);

            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);

            // float alpha = 1f -  Math.abs(position) * (1 - );
            float alpha = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - Math.abs(position));
            page.setAlpha(alpha);//透明度
        } else {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setAlpha(MIN_ALPHA);
        }
    }
}
