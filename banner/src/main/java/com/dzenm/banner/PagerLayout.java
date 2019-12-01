package com.dzenm.banner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.dzenm.banner.impl.IView;
import com.dzenm.banner.impl.OnItemClickListener;
import com.dzenm.banner.impl.PageTransformer;
import com.dzenm.banner.impl.TransformerStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dzenm
 * @date 2019-08-09 21:28
 */
public class PagerLayout extends RelativeLayout implements IView, View.OnClickListener, ViewPager.OnPageChangeListener {

    final static int LEFT_PAGE = 0;         // 左边显示页（根据index来调整左边页应该显示的图片）
    final static int CENTER_PAGE = 1;       // 中间显示页（永远停留在本页）
    final static int RIGHT_PAGE = 2;        // 右边显示页（根据index来调整右边页应该显示的图片）
    final static int COUNT_PAGE = 3;        // 创建页面对象的个数

    protected Activity mActivity;

    private ViewPager mViewPager;
    private RelativeLayout.LayoutParams mViewPagerParams;

    /**
     * 滑动的页面View, 可以直接使用ImageView做轮播图, 也可以使用单独的View
     */
    protected List<View> mViews;

    /**
     * 创建页面View的数量
     */
    protected int mImageCount;

    /**
     * 当前显示的View真正的位置, 由于在循环的时候, 只创建了三个页面, 必须动态的调整页面和图片之间的位置
     * 所以当前显示的View主要作用在此, 不循环的时候, 根据图片的个数创建View, 此时当前位置即图片所在位置
     */
    private int mCurrentImagePosition;

    /**
     * View之间的外边间距值, 用于使用一些其它的效果时, 配合使用
     * 默认的外边距值为8dp, {@link #setImageMargin(int)}
     */
    private int mImageMargin;

    /**
     * ViewPager外边距, 建议配合 {@link #setGallery(boolean)} 一起使用
     * 设置上下的margin {@link #setPagerMarginVertical(int)}
     * 设置左右的margin {@link #setPagerMarginHorizontal(int)}
     */
    private int[] mPagerMargins = new int[4];

    /**
     * 切换的效果类型, 当切换类型为STYLE_COVER时, 使用gallery方式会无效果， 不建议混合使用
     * 设置的类型详见 {@link TransformerStyle}
     */
    private @TransformerStyle
    int mTransformerStyle;

    /**
     * 页面跳转时的view动画 {@link #setPageTransformer(PageTransformer)}}
     */
    private PageTransformer mPageTransformer;

    /**
     * 定时器, 定时切换页面
     */
    private Timer mTimer;

    /**
     * 是否循环显示页面 {@link #setLoop(boolean)} )}
     */
    private boolean isLoop;

    /**
     * 是否显示画廊效果 {@link #setGallery(boolean)}
     */
    private boolean isGallery;

    /**
     * Item点击事件 {@link #setOnItemClickListener(OnItemClickListener)}
     */
    private OnItemClickListener onItemClickListener;

    public PagerLayout(Context context) {
        this(context, null);
    }

    public PagerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PagerLayout);
        isLoop = t.getBoolean(R.styleable.PagerLayout_loop, true);
        isGallery = t.getBoolean(R.styleable.PagerLayout_gallery, false);
        mImageMargin = (int) t.getDimension(R.styleable.PagerLayout_image_margin, dp2px(8));
        mPagerMargins[0] = mPagerMargins[2] = (int) t.getDimension(
                R.styleable.PagerLayout_pager_margin_horizontal, dp2px(0));
        mPagerMargins[1] = mPagerMargins[3] = (int) t.getDimension(
                R.styleable.PagerLayout_pager_margin_vertical, dp2px(0));
        mTransformerStyle = t.getInt(R.styleable.PagerLayout_transformerStyle,
                TransformerStyle.STYLE_NONE);
        t.recycle();
        initializeView(context);
    }

    private void initializeView(Context context) {
        mViewPagerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager = new ViewPager(context);
        mViewPager.setLayoutParams(mViewPagerParams);
        addView(mViewPager);
    }

    public PagerLayout setImageMargin(int imageMargin) {
        mImageMargin = dp2px(imageMargin);
        return this;
    }

    public PagerLayout setPagerMarginHorizontal(int horizontal) {
        mPagerMargins[0] = horizontal;
        mPagerMargins[2] = horizontal;
        return this;
    }

    public PagerLayout setPagerMarginVertical(int vertical) {
        mPagerMargins[1] = vertical;
        mPagerMargins[3] = vertical;
        return this;
    }

    public PagerLayout setLoop(boolean loop) {
        isLoop = loop;
        return this;
    }

    public PagerLayout setGallery(boolean gallery) {
        isGallery = gallery;
        return this;
    }

    public PagerLayout setOnItemClickListener(OnItemClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
        return this;
    }

    public PagerLayout setTransformerStyle(@TransformerStyle int transformerStyle) {
        mTransformerStyle = transformerStyle;
        return this;
    }

    public PagerLayout setPageTransformer(PageTransformer transformer) {
        mPageTransformer = transformer;
        return this;
    }

    public void play() {
        play(2);
    }

    /**
     * 只有循环页面时才可以设置自动播放, 并且需要在{@link #build()}方法之后
     */
    public void play(int period) {
        if (isLoop) setLoopToPlay(period);
    }

    /**
     * 循环播放
     */
    private void setLoopToPlay(int period) {
        if (mTimer == null) mTimer = new Timer();
        mTimer.schedule(mTimerTask, period * 1000, period * 1000);
    }

    /**
     * 防止内存泄露
     */
    public void destroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 跳转到下一页
     */
    public void nextPage() {
        // 因为位置始终为1 那么下一页就始终为2
        mViewPager.setCurrentItem(RIGHT_PAGE, true);
    }

    /**
     * 跳转到上一页
     */
    public void lastPage() {
        // 因为位置始终为1 那么上一页就始终为0
        mViewPager.setCurrentItem(LEFT_PAGE, true);
    }

    /**
     * 定时跳转
     */
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextPage();
                }
            });
        }
    };

    /**
     * 该方法会进行一些配置, 包括ViewPager的配置, View的创建, 以及Adapter的设置
     * 进行配置完之后, 最后调用该方法创建一个多页面滑动显示的View
     */
    public PagerLayout build() {
        buildViewPager();
        buildView();
        mViewPager.setPageTransformer(true,
                new Transformer(mTransformerStyle, mViewPager, mPageTransformer));
        mViewPager.setAdapter(new ViewPagerAdapter(mViews));                                  // 设置ViewPager适配器
        mViewPager.setCurrentItem(isLoop ? CENTER_PAGE : LEFT_PAGE);
        mViewPager.addOnPageChangeListener(this);                        // 监听ViewPager滑动
        return this;
    }

    /**
     * 创建ViewPager, 以及一些ViewPager的设置
     */
    protected void buildViewPager() {
        if (isGallery) {
            setClipChildren(false);
            mViewPager.setClipChildren(false);
        }
        mViewPagerParams.setMargins(dp2px(mPagerMargins[0]), dp2px(mPagerMargins[1]),
                dp2px(mPagerMargins[2]), dp2px(mPagerMargins[3]));
    }

    /**
     * 创建View
     */
    private void buildView() {
        int length = isLoop ? COUNT_PAGE : mImageCount;
        mViews = new ArrayList<>();
        createView(mViews, isLoop, length);
    }

    @Override
    public void createView(List<View> views, boolean isLoop, int length) {
        for (int i = 0; i < length; i++) {
            views.add(getView(true));
            int index = isLoop ? (i == 0 ? length - 1 : i - 1) : i;
            adjustViewPosition(i, index);
        }
    }

    protected View getView(boolean margin) {
        ImageView view = new ImageView(mActivity);
        view.setOnClickListener(this);
        if (!isGallery)
            if (margin)
                view.setPadding(mImageMargin, mImageMargin, mImageMargin, mImageMargin);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        return view;
    }

    protected void adjustViewPosition(int viewPosition, int position) {
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) onItemClickListener.onItemClick(mCurrentImagePosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0) {  // positionOffset等于0时处于静止, 静止时调整页面
            if (isLoop) {
                if (position == CENTER_PAGE) return;
                setLoopViewPosition(position);
                setLoopImagePosition();
            } else {
                mCurrentImagePosition = position;
            }
        } else {                    // 在滑动时监听滑动的偏移量
            onIndicatorBehavior(isLoop, position, positionOffset, mCurrentImagePosition);
        }
    }

    protected void onIndicatorBehavior(boolean isLoop, int position,
                                       float positionOffset, int currentViewPosition) {

    }

    /**
     * 当循环的时候在每次滑动时改变currentPage
     */
    private void setLoopViewPosition(int position) {
        // 初始 mCurrentImagePosition 为0, position为1, 由于滑动时进入直接判断
        mCurrentImagePosition = position > CENTER_PAGE ? mCurrentImagePosition + 1 : mCurrentImagePosition - 1;
        // 该if语句在于判断循环时, 在 mCurrentImagePosition == 0 时左滑 mCurrentImagePosition == -1
        // 表示 mCurrentImagePosition 为最后一页, 这用于重置页面位置, 同理, mCurrentImagePosition == mSize - 1也是如此
        if (mCurrentImagePosition == -1) {              // 起始页左滑，将左边那页设置为最后一页
            mCurrentImagePosition = mImageCount - 1;    // 最后一页
        } else if (mCurrentImagePosition == mImageCount) {  // 最后一页右滑，将右边那页设置为起始页
            mCurrentImagePosition = 0;                      // 第一页
        }
    }

    /**
     * 当循环的时候在每次滑动之后对图片重新调整
     */
    private void setLoopImagePosition() {
        // 设置左页的数据
        // 判断当前位置是否为数据起始位置，如果是（即0）将左页的数据设置为最后一个数据
        if (mCurrentImagePosition == 0) {
            adjustViewPosition(LEFT_PAGE, mImageCount - 1);
        } else {
            adjustViewPosition(LEFT_PAGE, mCurrentImagePosition - 1);
        }

        // 设置中间页的数据
        adjustViewPosition(CENTER_PAGE, mCurrentImagePosition);

        // 设置右页的数据
        // 判断当前位置是否为数据末尾，如果是（即size-1）将右边的数据设置为第一个数据
        if (mCurrentImagePosition == mImageCount - 1) {
            adjustViewPosition(RIGHT_PAGE, 0);
        } else {
            adjustViewPosition(RIGHT_PAGE, mCurrentImagePosition + 1);
        }
        /*
         * 滑动结束后将当前页设置为第二页
         * 即ViewPager的当前显示position，本来为1，每次滑动之后会变为2，需要手动将它设置为1
         */
        mViewPager.setCurrentItem(CENTER_PAGE, false);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 当用手指滑动时，在手指滑动的时刻触发state==1, 滑动停止时，先调用state==2，在调用state==0
        // 当不用手指滑动时，滑动的时刻不会调用state==1, 直接等滑动结束时，先调用state==2，在调用state==0
    }

    public static int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }
}