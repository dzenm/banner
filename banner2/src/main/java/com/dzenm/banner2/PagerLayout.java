package com.dzenm.banner2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.dzenm.banner2.impl.IView;
import com.dzenm.banner2.impl.ImageLoader;
import com.dzenm.banner2.impl.OnItemClickListener;
import com.dzenm.banner2.impl.OnPageSelectedListener;
import com.dzenm.banner2.impl.PageTransformer;
import com.dzenm.banner2.impl.TransformerStyle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dzenm
 * @date 2019-08-09 21:28
 */
public class PagerLayout extends RelativeLayout implements IView, View.OnClickListener {

    private static final String TAG = PagerLayout.class.getSimpleName();

    protected Activity mActivity;
    private ViewPager mViewPager;
    private LayoutParams mViewPagerParams;

    private ViewPagerAdapter mAdapter;

    private ImageLoader mImageLoader;

    /**
     * Item view数据, 可以使用url, bitmap, drawable, resource作为图片显示
     */
    private List mData;

    /**
     * View之间的外边间距值, 用于使用一些其它的效果时, 配合使用
     * 默认的外边距值为8dp, {@link #setItemViewMargin(int)}
     */
    private int mItemViewMargin;

    /**
     * 切换的效果类型, 当切换类型为STYLE_COVER时, 使用gallery方式会无效果， 不建议混合使用
     * 设置的类型详见 {@link TransformerStyle}
     */
    private @TransformerStyle
    int mTransformerStyle = TransformerStyle.STYLE_NONE;

    /**
     * 定时器, 定时切换页面
     */
    private Timer mTimer;

    /**
     * 是否循环显示页面 {@link #loop()}
     */
    private boolean isLoop;

    /**
     * 是否显示画廊效果 {@link #gallery()}
     */
    private boolean isGallery;

    /**
     * 页面跳转时的view动画 {@link #setTransformer(PageTransformer)}
     */
    private PageTransformer mPageTransformer;

    /**
     * Item点击事件 {@link #setOnItemClickListener(OnItemClickListener)}
     */
    private OnItemClickListener onItemClickListener;

    private OnPageSelectedListener mOnPageSelectedListener;

    public PagerLayout(Context context) {
        this(context, null);
    }

    public PagerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;

        @SuppressLint("Recycle") TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PagerLayout);
        isLoop = t.getBoolean(R.styleable.PagerLayout_loop, false);
        isGallery = t.getBoolean(R.styleable.PagerLayout_gallery, false);
        mItemViewMargin = (int) t.getDimension(R.styleable.PagerLayout_itemViewMargin, dp2px(8));

        t.recycle();
        initializeViewPager(context);
    }

    private void initializeViewPager(Context context) {
        mViewPagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager = new ViewPager(context);
        mViewPager.setLayoutParams(mViewPagerParams);
        addView(mViewPager);
    }

    /************************************* 以下为自定义方法 *********************************/

    public PagerLayout load(List data) {
        mData = data;
        Log.d(TAG, "data size is " + data.size());
        return this;
    }

    public PagerLayout into(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public PagerLayout loop() {
        isLoop = true;
        return this;
    }

    public PagerLayout gallery() {
        isGallery = true;
        return this;
    }

    public PagerLayout setItemViewMargin(int itemViewMargin) {
        mItemViewMargin = dp2px(itemViewMargin);
        return this;
    }

    public PagerLayout setTransformerStyle(int transformerStyle) {
        mTransformerStyle = transformerStyle;
        return this;
    }

    public PagerLayout setOnItemClickListener(OnItemClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
        return this;
    }

    public PagerLayout setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
        return this;
    }

    public PagerLayout setTransformer(PageTransformer pageTransformer) {
        mPageTransformer = pageTransformer;
        mTransformerStyle = TransformerStyle.STYLE_DIY;
        return this;
    }

    public ViewPager getViewPager() {
        return mViewPager;
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
        mAdapter.nextPage(true);
    }

    /**
     * 跳转到上一页
     */
    public void lastPage() {
        mAdapter.lastPage(true);
    }

    /************************************* 以下为实现的细节 *********************************/

    /**
     * 该方法会进行一些配置, 包括ViewPager的配置, View的创建, 以及Adapter的设置
     * 进行配置完之后, 最后调用该方法创建一个多页面滑动显示的View
     */
    public PagerLayout build() {
        mAdapter = new ViewPagerAdapter(mData, mViewPager, this, isLoop);
        buildViewPager(mAdapter);
        return this;
    }

    /**
     * 创建ViewPager, 以及一些ViewPager的设置
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void buildViewPager(ViewPagerAdapter viewPagerAdapter) {
        if (isGallery) {
            setClipChildren(false);
            setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mViewPager.dispatchTouchEvent(event);
                }
            });
            mViewPager.setClipChildren(false);
        }

        mViewPager.setPageTransformer(false,
                new ViewPagerTransformer(mTransformerStyle, mViewPager, mPageTransformer));

        viewPagerAdapter.setOnPageSelectedListener(mOnPageSelectedListener);
    }

    @Override
    public View createItemView(Object object, int position) {
        ImageView view = (ImageView) getView();
        mImageLoader.onLoader(view, mData.get(position));
        return view;
    }

    protected View getView() {
        ImageView view = new ImageView(mActivity);
        view.setOnClickListener(this);
        if (mTransformerStyle != TransformerStyle.STYLE_3D) {
            view.setPadding(mItemViewMargin, mItemViewMargin, mItemViewMargin, mItemViewMargin);
        }
        view.setLayoutParams(mViewPagerParams);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(mAdapter.getCurrentRealPosition());
        }
    }

    public static int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                Resources.getSystem().getDisplayMetrics());
    }
}
