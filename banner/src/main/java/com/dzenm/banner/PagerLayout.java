package com.dzenm.banner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dzenm
 * @date 2019-08-09 21:28
 */
public class PagerLayout extends RelativeLayout implements IView, View.OnClickListener, PagerChangerHelper.OnViewChangeListener {

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
    protected int mViewCount;

    /**
     * 当前显示的View真正的位置, 由于在循环的时候, 只创建了三个页面, 必须动态的调整页面和图片之间的位置
     * 所以当前显示的View主要作用在此, 不循环的时候, 根据图片的个数创建View, 此时当前位置即图片所在位置
     */
    private int mCurrentViewPosition;

    /**
     * View之间的外边间距值, 用于使用一些其它的效果时, 配合使用
     * 默认的外边距值为8dp, {@link #setPagerMargin(int)}
     */
    private int mViewMargin = dp2px(8);

    /**
     * ViewPager外边距, 建议配合 {@link #setGallery(boolean)} 一起使用
     * 设置上下的margin {@link #setViewPagerMarginVertical(int)}
     * 设置左右的margin {@link #setViewPagerMarginHorizontal(int)}
     */
    private int[] mViewPagerMargins = new int[4];

    /**
     * 定时器, 定时切换页面
     */
    private Timer mTimer;

    /**
     * 是否循环显示页面 {@link #setLoop(boolean)} )}
     */
    private boolean isLoop = true;

    /**
     * 是否显示画廊效果 {@link #setGallery(boolean)}
     */
    private boolean isGallery = false;

    /**
     * 自定义页面 {@link #setIView(IView)}
     */
    private IView mIView;

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
        initializeView(context);
    }

    private void initializeView(Context context) {
        mViewPagerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager = new ViewPager(context);
        mViewPager.setLayoutParams(mViewPagerParams);
        addView(mViewPager);
    }

    public <T extends PagerLayout> T setPagerMargin(int viewMargin) {
        mViewMargin = dp2px(viewMargin);
        return (T) this;
    }

    public <T extends PagerLayout> T setViewPagerMarginHorizontal(int horizontal) {
        mViewPagerMargins[0] = horizontal;
        mViewPagerMargins[2] = horizontal;
        return (T) this;
    }

    public <T extends PagerLayout> T setViewPagerMarginVertical(int vertical) {
        mViewPagerMargins[1] = vertical;
        mViewPagerMargins[3] = vertical;
        return (T) this;
    }

    public <T extends PagerLayout> T setLoop(boolean loop) {
        isLoop = loop;
        return (T) this;
    }

    public <T extends PagerLayout> T setGallery(boolean gallery) {
        isGallery = gallery;
        return (T) this;
    }

    public <T extends PagerLayout> T setOnItemClickListener(OnItemClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
        return (T) this;
    }

    public <T extends PagerLayout> T setIView(IView iView) {
        mIView = iView;
        return (T) this;
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
        mViewPager.setCurrentItem(PagerChangerHelper.RIGHT_PAGE, true);
    }

    /**
     * 跳转到上一页
     */
    public void lastPage() {
        // 因为位置始终为1 那么上一页就始终为0
        mViewPager.setCurrentItem(PagerChangerHelper.LEFT_PAGE, true);
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
    public <T extends PagerLayout> T build() {
        PagerChangerHelper pagerChangerHelper = new PagerChangerHelper();
        buildViewPager(pagerChangerHelper);
        buildView();
        pagerChangerHelper.setAdapter(new ViewPagerAdapter(mViews));
        return (T) this;
    }

    /**
     * 创建ViewPager, 以及一些ViewPager的设置
     */
    protected void buildViewPager(PagerChangerHelper pagerChangerHelper) {
        mViewPagerParams.setMargins(dp2px(mViewPagerMargins[0]), dp2px(mViewPagerMargins[1]),
                dp2px(mViewPagerMargins[2]), dp2px(mViewPagerMargins[3]));
        if (isGallery) {
            setClipChildren(false);
            mViewPager.setClipChildren(false);
            mViewPager.setPageTransformer(true, new PagerLayoutTransformer());
        }
        pagerChangerHelper.setOnViewChangeListener(this);
        pagerChangerHelper.setViewCount(mViewCount);
        pagerChangerHelper.setViewPager(mViewPager);
        mCurrentViewPosition = pagerChangerHelper.getCurrentViewPosition();
        pagerChangerHelper.setLoop(isLoop);
    }

    /**
     * 创建View
     */
    private void buildView() {
        int length = isLoop ? PagerChangerHelper.COUNT_PAGE : mViewCount;
        mViews = new ArrayList<>();
        if (mIView == null)
            createView(mViews, isLoop, length);
        else
            mIView.createView(mViews, isLoop, length);
    }

    @Override
    public void createView(List<View> views, boolean isLoop, int length) {
        for (int i = 0; i < length; i++) {
            views.add(getView(true));
            int index = isLoop ? (i == 0 ? length - 1 : i - 1) : i;
            onViewChange(i, index);
        }
    }

    protected View getView(boolean isAddMargin) {
        ImageView view = new ImageView(mActivity);
        view.setOnClickListener(this);
        if (!isGallery)
            if (isAddMargin)
                view.setPadding(mViewMargin, mViewMargin, mViewMargin, mViewMargin);
        if (view instanceof ImageView)
            view.setScaleType(ImageView.ScaleType.FIT_XY);
        return view;
    }

    @Override
    public void onViewChange(int currentView, int position) {
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) onItemClickListener.onItemClick(mCurrentViewPosition);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                Resources.getSystem().getDisplayMetrics());
    }
}
