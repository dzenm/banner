package com.dzenm;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.banner.BannerLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerLayout loopBanner = findViewById(R.id.banner_loop);
        BannerLayout unLoopBanner = findViewById(R.id.banner_unloop);
        BannerLayout galleryBanner = findViewById(R.id.banner_gallery);
        int[] images = new int[]{
                R.drawable.one, R.drawable.two,
                R.drawable.three, R.drawable.four,
                R.drawable.five, R.drawable.six,
                R.drawable.seven
        };
        loopBanner.setLoop(true)
                .setIndicator(true)
                .setImage(images)
                .build();
        unLoopBanner.setLoop(false)
                .setIndicator(true)
                .setIndicatorResource(R.drawable.select_indicator, R.drawable.unselect_indicator)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .setIndicatorMargin(0, 0, 40, 30)
                .setImage(images)
                .build();
        galleryBanner.setLoop(true)
                .setIndicator(true)
                .setViewPagerMarginHorizontal(40)
                .setGallery(true)
                .addIndicatorRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                .setIndicatorMargin(0, 30, 40, 0)
                .setImage(images)
                .build();


    }
}
