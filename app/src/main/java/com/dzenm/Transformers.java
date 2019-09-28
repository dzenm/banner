package com.dzenm;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.banner.BannerLayout;
import com.dzenm.banner.PagerLayout;
import com.dzenm.banner.impl.PageTransformer;

/**
 * @author dzenm
 * @date 2019-08-28 00:09
 */
public class Transformers implements PageTransformer {

    private static final float CENTER_PAGE_SCALE = 1f;

    private void foldPager(View view, @NonNull ViewPager viewPager, float position) {
        int pagerWidth = viewPager.getWidth();
        int offscreenPageLimit = viewPager.getOffscreenPageLimit();
        float horizontalOffsetBase =
                (pagerWidth - pagerWidth * CENTER_PAGE_SCALE) / 2 / offscreenPageLimit + PagerLayout.dp2px(5);

        if (position >= offscreenPageLimit || position <= -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        if (position >= 0) {
            float translationX = (horizontalOffsetBase - view.getWidth()) * position;
            view.setTranslationX(translationX);
        }
        if (position > -1 && position < 0) {
            float rotation = position * 30;
            view.setRotation(rotation);
            view.setAlpha((position * position * position + 1));
        } else if (position > offscreenPageLimit - 1) {
            view.setAlpha((float) (1 - position + Math.floor(position)));
        } else {
            view.setRotation(0);
            view.setAlpha(1);
        }
        if (position == 0) {
            view.setScaleX(CENTER_PAGE_SCALE);
            view.setScaleY(CENTER_PAGE_SCALE);
        } else {
            float scaleFactor = Math.min(CENTER_PAGE_SCALE - position * 0.1f, CENTER_PAGE_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5);
    }

    @Override
    public void transformPage(@NonNull View page, @NonNull ViewPager viewPager, float position) {
        foldPager(page, viewPager, position);
    }
}
