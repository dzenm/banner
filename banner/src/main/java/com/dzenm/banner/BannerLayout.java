package com.dzenm.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dzenm.banner.impl.IIndicator;
import com.dzenm.banner.impl.ImageLoader;

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
public class BannerLayout extends PagerLayout implements IIndicator, ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * 指示灯的Layout和选中的指示灯
     */
    private RelativeLayout.LayoutParams mRootLayoutParams;
    private RelativeLayout mRootLayout;
    private LinearLayout mIndicatorLayout;
    private ImageView mIndicatorImageView;

    private Object[] mImages;

    /**
     * 是否显示指示灯 {@link #setIndicator(boolean)}
     */
    private boolean isShowIndicator;

    /**
     * 指示器的图片 {@link #setIndicatorResource(int, int)} )}
     */
    private int mSelectedIndicator, mUnSelectedIndicator;

    /**
     * 两个指示灯的间距
     */
    private float mIndicatorDistance;

    private ImageLoader mImageLoader;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.BannerLayout);
        mSelectedIndicator = t.getInteger(R.styleable.BannerLayout_selectorIndicator, R.drawable.select);
        mUnSelectedIndicator = t.getInteger(R.styleable.BannerLayout_unSelectorIndicator, R.drawable.unselect);
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
        RelativeLayout.LayoutParams unIndicatorParams = new RelativeLayout.LayoutParams(
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

    public BannerLayout load(Object[] images) {
        mImages = images;
        mImageCount = images.length;
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

    @Override
    protected void buildViewPager() {
        if (isShowIndicator) {
            // 监听小圆点滑动的跳转
            mIndicatorImageView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            createIndicator(mIndicatorLayout, dp2px(4), mImageCount);
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
    protected void adjustViewPosition(int viewPosition, int position) {
        mImageLoader.onLoader(mViews.get(viewPosition), mImages[position]);
    }

    @Override
    protected void onIndicatorBehavior(boolean isLoop, int position,
                                       float positionOffset, int currentViewPosition) {
        if (isShowIndicator) {  // 提示的小圆点
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIndicatorImageView.getLayoutParams();
            if (isLoop) {
                float offset = indicatorBehavior(position, positionOffset, currentViewPosition, mImageCount);
                params.leftMargin = (int) (offset * mIndicatorDistance);
            } else {
                params.leftMargin = (int) ((positionOffset + position) * mIndicatorDistance);
            }
            mIndicatorImageView.setLayoutParams(params);
        }
    }

    private float indicatorBehavior(int position, float offset, int currentPosition, int size) {
        float offsetDistance = 0;
        if (position == 0) {                    // 左滑(offset从1.0-0.0结束)
            if (currentPosition == 0) {         // 是否是第一个向左滑动，并且显示到最后一个
                if (offset > 0.5) {
                    offsetDistance = offset - 1;
                } else {
                    offsetDistance = (size - 1) + offset;
                }
            } else {
                offsetDistance = currentPosition - (1 - offset);
            }
        } else if (position == 1) {             // 右滑(offset从0.0-1.0结束)
            if (currentPosition == size - 1) {  // 是否是最后一个向左滑动，并且显示到第一个
                if (offset < 0.5) {
                    offsetDistance = (size - 1) + offset;
                } else {
                    offsetDistance = offset - 1;
                }
            } else {
                offsetDistance = currentPosition + offset;
            }
        }
        return offsetDistance;
    }

    @Override
    public void onGlobalLayout() {
        mIndicatorDistance = mIndicatorLayout.getChildAt(1).getLeft() - mIndicatorLayout.getChildAt(0).getLeft();    // 两个圆点之间的距离
        mIndicatorImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}