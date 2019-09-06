package com.dzenm;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.banner.BannerLayout;
import com.dzenm.banner.PagerLayout;
import com.dzenm.banner.TransformerStyle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Integer> list = new ArrayList<>();
        list.add(R.drawable.one);
        list.add(R.drawable.two);
        list.add(R.drawable.three);
        list.add(R.drawable.four);
        list.add(R.drawable.five);
        list.add(R.drawable.six);
        list.add(R.drawable.seven);

        BannerLayout loopBanner = findViewById(R.id.banner_loop);
        loopBanner.setLoop(true)
                .setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .setTransformerStyle(TransformerStyle.STYLE_NONE)
                .load(list)
                .build();
        final BannerLayout unLoopBanner = findViewById(R.id.banner_unloop);
        unLoopBanner.setLoop(false)
                .setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .setIndicatorResource(R.drawable.select_indicator, R.drawable.unselect_indicator)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .setIndicatorMargin(0, 0, 40, 30)
                .setOnPageSelectedListener(new PagerLayout.OnPageSelectedListener() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position == list.size() - 1) {
                            unLoopBanner.setIndicatorVisible(View.GONE);
                        } else {
                            unLoopBanner.setIndicatorVisible(View.VISIBLE);
                        }
                    }
                })
                .load(list)
                .build();

        BannerLayout foldBanner = findViewById(R.id.banner_fold);
        foldBanner.setLoop(false)
                .setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .setTransformerStyle(TransformerStyle.STYLE_COVER)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .setIndicatorMargin(0, 30, 40, 0)
                .load(list)
                .build();

        BannerLayout galleryBanner = findViewById(R.id.banner_gallery);
        galleryBanner.setLoop(true)
                .setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .setViewPagerMarginHorizontal(40)
                .setGallery(true)
                .setTransformerStyle(TransformerStyle.STYLE_3D)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .setIndicatorMargin(0, 30, 40, 0)
                .load(list)
                .build();

        BannerLayout diyBanner = findViewById(R.id.banner_diy);
        diyBanner.setLoop(false)
                .setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .setTransformerStyle(TransformerStyle.STYLE_FOLD)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .load(list)
                .build();
    }
}
