package com.dzenm.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * @date 2019-08-09 22:01
 * <pre>
 * 在layout中添加布局
 * <com.dzenm.banner.BannerLayout
 *       android:id="@+id/banner_gallery"
 *       android:layout_width="match_parent"
 *       android:layout_height="180dp" />
 *  final List<Integer> list = new ArrayList<>();
 *  list.add(R.drawable.one);
 *  list.add(R.drawable.two);
 *  list.add(R.drawable.three);
 *  list.add(R.drawable.four);
 *  list.add(R.drawable.five);
 *  list.add(R.drawable.six);
 *  list.add(R.drawable.seven);
 *
 * 必须要自定义ImageLoader, 建议使用Glide
 * public class MyImageLoader implements ImageLoader {
 *     @Override
 *     public void onLoader(View view, Object imageResource) {
 *         RoundedCorners rc = new RoundedCorners(8);
 *         RequestOptions options = RequestOptions.bitmapTransform(rc);
 *         Glide.with(view.getContext()).load(imageResource).apply(options).into((ImageView) view);
 *     }
 * }
 *
 * 可循环的banner
 * BannerLayout loopBanner = findViewById(R.id.banner_loop);
 * loopBanner.setLoop(true)
 *     .setImageLoader(new MyImageLoader())
 *     .setIndicator(true)
 *     .load(list)
 *     .build();
 *
 * 不可循环的banner
 * BannerLayout unLoopBanner = findViewById(R.id.banner_unloop);
 * unLoopBanner.setLoop(false)
 *     .setIndicator(true)
 *     .setImageLoader(new MyImageLoader())
 *     .setIndicatorResource(R.drawable.select_indicator, R.drawable.unselect_indicator)
 *     .addIndicatorRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
 *     .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
 *     .setIndicatorMargin(0, 0, 40, 30)
 *     .load(list)
 *     .build();
 *
 * 画廊效果
 * BannerLayout galleryBanner = findViewById(R.id.banner_gallery);
 * galleryBanner.setLoop(true)
 *     .setIndicator(true)
 *     .setImageLoader(new MyImageLoader())
 *     .setViewPagerMarginHorizontal(40)
 *     .setGallery(true)
 *     .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
 *     .setIndicatorMargin(0, 30, 40, 0)
 *     .load(list)
 *     .build();
 * </pre>
 */
public class BannerLayout extends PagerLayout implements IIndicator {

    /**
     * 指示灯的Layout和选中的指示灯
     */
    private RelativeLayout.LayoutParams mRootLayoutParams;
    private RelativeLayout mRootLayout;
    private LinearLayout mIndicatorLayout;
    private ImageView mIndicatorImageView;

    /**
     * 图片, 可以使用url, bitmap, drawable, resource
     */
    private List mImages;

    /**
     * 是否显示指示灯 {@link #setIndicator(boolean)}
     */
    private boolean isShowIndicator;

    /**
     * 指示器的图片 {@link #setIndicatorResource(int, int)} )}
     */
    private int mSelectedIndicator, mUnSelectedIndicator;

    /**
     * 图片加载, 使用第三方框架加载 {@link ImageLoader}
     */
    private ImageLoader mImageLoader;

    /**
     * 自定义指示器 {@link #setIIndicator(IIndicator)}
     */
    private IIndicator mIIndicator;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        @SuppressLint("Recycle") TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.BannerLayout);
        mSelectedIndicator = t.getInteger(R.styleable.BannerLayout_selectorIndicator, R.drawable.select);
        mUnSelectedIndicator = t.getInteger(R.styleable.BannerLayout_selectorIndicator, R.drawable.unselect);
        isShowIndicator = t.getBoolean(R.styleable.BannerLayout_showIndicator, true);

        t.recycle();
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
        LinearLayout.LayoutParams unIndicatorParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mIndicatorLayout = new LinearLayout(context);
        mIndicatorLayout.setLayoutParams(unIndicatorParams);
        mIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
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
    public BannerLayout setTransformerStyle(int transformerStyle) {
        return super.setTransformerStyle(transformerStyle);
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

    @Override
    public BannerLayout setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        return super.setOnPageSelectedListener(onPageSelectedListener);
    }

    @Override
    public BannerLayout setTransformer(PageTransformer pageTransformer) {
        return super.setTransformer(pageTransformer);
    }

    public BannerLayout load(List lists) {
        mImages = lists;
        mViewCount = lists.size();
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

    public BannerLayout setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public BannerLayout setIIndicator(IIndicator iIndicator) {
        mIIndicator = iIndicator;
        return this;
    }

    public BannerLayout setIndicatorVisible(int visible) {
        mIndicatorLayout.setVisibility(visible);
        mIndicatorImageView.setVisibility(visible);
        return this;
    }

    @Override
    public BannerLayout build() {
        return super.build();
    }

    @Override
    protected void buildViewPager(PagerHelper pagerHelper) {
        super.buildViewPager(pagerHelper);
        if (isShowIndicator) {
            pagerHelper.setShowIndicator(true);
            pagerHelper.setIndicatorLayout(mIndicatorLayout);
            pagerHelper.setIndicatorImageView(mIndicatorImageView);
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
    public void onViewPagerChange(int viewPosition, int position) {
        super.onViewPagerChange(viewPosition, position);
        mImageLoader.onLoader(mViews.get(viewPosition), mImages.get(position));
    }
}