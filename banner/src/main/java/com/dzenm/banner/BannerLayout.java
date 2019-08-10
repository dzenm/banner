package com.dzenm.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * @date 2019-08-09 22:01
 */
public class BannerLayout extends PagerLayout implements IIndicator {

    private static final int IMAGE_URL = 1;
    private static final int IMAGE_BITMAP = 2;
    private static final int IMAGE_RESOURCE = 3;

    /**
     * 指示灯的Layout和选中的指示灯
     */
    private RelativeLayout.LayoutParams mRootLayoutParams;
    private RelativeLayout mRootLayout;
    private LinearLayout mIndicatorLayout;
    private ImageView mIndicatorImageView;

    /**
     * 图片
     */
    private String[] mUrl;
    private Bitmap[] mBitmap;
    private int[] mImageResource;

    /**
     * 图片类型
     */
    private int mImageType;

    /**
     * 图片的圆角值 {@link #setRadius(int)} )}
     */
    private int mRadius = 8;

    /**
     * 是否显示指示灯 {@link #setIndicator(boolean)}
     */
    private boolean isShowIndicator = false;

    /**
     * 自定义指示器 {@link #setIIndicator(IIndicator)}
     */
    private IIndicator mIIndicator;

    /**
     * 指示器的图片 {@link #setIndicatorResource(int, int)} )}
     */
    private int mSelectedIndicator = R.drawable.select;
    private int mUnSelectedIndicator = R.drawable.unselect;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context);
    }

    private void initializeView(Context context) {
        // 指示器外层布局
        mRootLayoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mRootLayout = new RelativeLayout(context);
        mRootLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        mRootLayoutParams.addRule(CENTER_HORIZONTAL);
        mRootLayoutParams.setMargins(0, 0, 0, dp2px(10));
        mRootLayout.setLayoutParams(mRootLayoutParams);
        addView(mRootLayout);

        // 未选中的一组指示器的布局
        RelativeLayout.LayoutParams unIndicatorParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mIndicatorLayout = new LinearLayout(context);
        mIndicatorLayout.setLayoutParams(unIndicatorParams);
        mIndicatorLayout.setLayoutDirection(LinearLayout.HORIZONTAL);
        mRootLayout.addView(mIndicatorLayout);

        // 选中的指示器
        RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mIndicatorImageView = new ImageView(context);
        mIndicatorImageView.setLayoutParams(indicatorParams);
        mRootLayout.addView(mIndicatorImageView);
    }

    @Override
    public BannerLayout setPagerMargin(int viewMargin) {
        return super.setPagerMargin(viewMargin);
    }

    @Override
    public BannerLayout setViewPagerMarginHorizontal(int horizontal) {
        return super.setViewPagerMarginHorizontal(horizontal);
    }

    @Override
    public BannerLayout setViewPagerMarginVertical(int vertical) {
        return super.setViewPagerMarginVertical(vertical);
    }

    @Override
    public BannerLayout setLoop(boolean loop) {
        return super.setLoop(loop);
    }

    @Override
    public BannerLayout setGallery(boolean gallery) {
        return super.setGallery(gallery);
    }

    @Override
    public BannerLayout setOnItemClickListener(OnItemClickListener itemClickListener) {
        return super.setOnItemClickListener(itemClickListener);
    }

    public BannerLayout setImage(String[] urls) {
        mUrl = urls;
        mViewCount = urls.length;
        mImageType = IMAGE_URL;
        return this;
    }

    public BannerLayout setImage(Bitmap[] bitmaps) {
        mBitmap = bitmaps;
        mViewCount = bitmaps.length;
        mImageType = IMAGE_BITMAP;
        return this;
    }

    public BannerLayout setImage(int[] imageResources) {
        mImageResource = imageResources;
        mViewCount = imageResources.length;
        mImageType = IMAGE_RESOURCE;
        return this;
    }

    public BannerLayout setRadius(int radius) {
        mRadius = radius;
        return this;
    }

    public BannerLayout setIndicator(boolean showIndicator) {
        isShowIndicator = showIndicator;
        return this;
    }

    public BannerLayout addIndicatorRule(int verb) {
        mRootLayoutParams.addRule(verb);
        return this;
    }

    public BannerLayout setIndicatorMargin(int left, int top, int right, int bottom) {
        mRootLayoutParams.setMargins(dp2px(left), dp2px(top), dp2px(right), dp2px(bottom));
        return this;
    }

    public BannerLayout setIndicatorResource(int selectedIndicator, int unSelectedIndicator) {
        mSelectedIndicator = selectedIndicator;
        mUnSelectedIndicator = unSelectedIndicator;
        return this;
    }

    public BannerLayout setIIndicator(IIndicator iIndicator) {
        mIIndicator = iIndicator;
        return this;
    }

    @Override
    public BannerLayout build() {
        return super.build();
    }

    @Override
    protected void buildViewPager(PagerChangerHelper pagerChangerHelper) {
        super.buildViewPager(pagerChangerHelper);
        if (isShowIndicator) {
            pagerChangerHelper.setShowIndicator(isShowIndicator);
            pagerChangerHelper.setIndicatorLayout(mIndicatorLayout);
            pagerChangerHelper.setIndicatorImageView(mIndicatorImageView);
            if (mIIndicator == null) {
                createIndicator(mIndicatorLayout, dp2px(4), mViewCount);
            } else {
                mIIndicator.createIndicator(mIndicatorLayout, dp2px(4), mViewCount);
            }
        } else {
            mRootLayout.setVisibility(GONE);
        }
    }

    @Override
    public void createIndicator(LinearLayout indicatorLayout, int indicatorMargin, int viewCount) {
        // 显示小圆点的imageView
        List<ImageView> imageViews = new ArrayList<>();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) indicatorLayout.getLayoutParams();
        params.setMargins(0, 0, indicatorMargin, 0);      // 设置小圆点左右之间的距离

        for (int i = 0; i < viewCount; i++) {
            ImageView imageView = (ImageView) getView(false);
            imageView.setLayoutParams(params);
            indicatorLayout.addView(imageView, params);
            imageViews.add(imageView);
            imageViews.get(i).setImageResource(mUnSelectedIndicator);   // 设置未选中小圆点样式
        }
        mIndicatorImageView.setImageResource(mSelectedIndicator);
    }

    @Override
    public void onViewChange(int currentView, int position) {
        RoundedCorners rc = new RoundedCorners(dp2px(mRadius));
        RequestOptions options = RequestOptions.bitmapTransform(rc);
        if (mImageType == IMAGE_URL) {
            Glide.with(mActivity).load(mUrl[position]).apply(options).into((ImageView) mViews.get(currentView));
        } else if (mImageType == IMAGE_BITMAP) {
            Glide.with(mActivity).load(mBitmap[position]).apply(options).into((ImageView) mViews.get(currentView));
        } else if (mImageType == IMAGE_RESOURCE) {
            Glide.with(mActivity).load(mImageResource[position]).apply(options).into((ImageView) mViews.get(currentView));
        }
    }
}