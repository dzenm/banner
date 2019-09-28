package com.dzenm.banner;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.dzenm.banner.impl.PageTransformer;

/**
 * @author dzenm
 * @date 2019-08-10 21:00
 */
class PagerTransformer implements ViewPager.PageTransformer {

    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.75f;

    private static final float CENTER_PAGE_SCALE = 0.8f;
    private int mOffscreenPageLimit;
    private ViewPager mViewPager;

    private @TransformerStyle
    int mStyle;

    private PageTransformer mPageTransformer;

    PagerTransformer(@TransformerStyle int style, @NonNull ViewPager viewPager,
                     PageTransformer pageTransformer) {
        mStyle = style;
        mViewPager = viewPager;
        mOffscreenPageLimit = mViewPager.getOffscreenPageLimit();
        mPageTransformer = pageTransformer;
    }

    /**
     * 当前页page为0, 之后的page位置叠加, 之前的page位置递减
     * 当向左滑动时，当前page的position值变化为[0~-1]，右边page的position值变化为[1~0]；
     * 当向右滑动时，当前page的position值变化为[0~1]，左边page的position值变化为[-1~0]；
     *
     * @param view
     * @param position
     */
    @Override
    public void transformPage(@NonNull View view, float position) {
        if (mStyle == TransformerStyle.STYLE_3D) {
            transformer3D(view, position);
        } else if (mStyle == TransformerStyle.STYLE_COVER) {
            transformerCover(view, position);
        } else if (mStyle == TransformerStyle.STYLE_FOLD) {
            foldPager(view, position);
        } else if (mStyle == TransformerStyle.STYLE_DIY) {
            mPageTransformer.transformPage(view, mViewPager, position);
        }
    }

    private void transformer3D(View page, float position) {
        if (position < -1) {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setAlpha(MIN_ALPHA);
        } else if (position < 1) {
            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            // [0, 1 ）相对于当前选中页，其右边第一页
            if (position > 0) page.setTranslationX(-scaleFactor);
                // [-1, 0) 相对于当前选中页，其左边的第一页
            else if (position < 0) page.setTranslationX(scaleFactor);

            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);

            float alpha = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - Math.abs(position));
            page.setAlpha(alpha);//透明度
        } else {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setAlpha(MIN_ALPHA);
        }
    }

    private void transformerCover(View page, float position) {
        if (position < -1) {
            page.setAlpha(0);
        } else if (position <= 0) {
            // 左右移动, 并且移除时变透明
            page.setAlpha(1 + position);
        } else if (position < 1) {
            // 去除左右移动效果
            page.setTranslationX(-page.getWidth() * position);
            // 进入时变大, 移除时变小
            page.setScaleY(1 - position / 2);
            page.setScaleX(1 - position / 2);
            page.setAlpha(1 - position);
        } else {
            page.setAlpha(0);
        }
    }

    private void foldPager(View view, float position) {
        int pagerWidth = mViewPager.getWidth();
        float horizontalOffsetBase = (pagerWidth - pagerWidth * CENTER_PAGE_SCALE) /
                2 / mOffscreenPageLimit;

        // 由于mOffscreenPageLimit为3, 所以只缓存当前显示的view,  因此为了查看缓存的
        // 后两张需要显示的view, 故mOffscreenPageLimit + 2
        if (position >= mOffscreenPageLimit + 2 || position <= -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        // 设置右边的view进行向右偏移
        if (position > 0) {
            float translationX = (horizontalOffsetBase - view.getWidth()) * position;
            view.setTranslationX(translationX);
        }

        if (position < 0) {
            // 当前显示的view移入时渐隐或渐显的动画
            float rotation = position * 30;
            view.setRotation(rotation);
            view.setAlpha(position * mOffscreenPageLimit + 1);
        } else if (position > 2) {
            // 当前view之后的第三个view渐隐的透明度变化
            view.setAlpha((float) (1 - position + Math.floor(position)));
        } else {
            // 当前view之后的第一个和第二个可见的view的透明度
            view.setRotation(0);
            view.setAlpha(1f);
        }

        // 不移动时view的缩放显示
        if (position == -1) {
            view.setScaleX(CENTER_PAGE_SCALE);
            view.setScaleY(CENTER_PAGE_SCALE);
        } else {
            float scaleFactor = Math.min(CENTER_PAGE_SCALE - position * 0.1f, CENTER_PAGE_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
        ViewCompat.setElevation(view, (mOffscreenPageLimit - position) * 5);
    }
}
