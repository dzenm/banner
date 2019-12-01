package com.dzenm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.banner.BannerLayout;
import com.dzenm.banner.impl.TransformerStyle;

import java.util.ArrayList;
import java.util.List;

public class BannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.one);
        list.add(R.drawable.two);
        list.add(R.drawable.three);
        list.add(R.drawable.four);
        list.add(R.drawable.five);
        list.add(R.drawable.six);
        list.add(R.drawable.seven);

        BannerLayout loopBanner = findViewById(R.id.banner_loop);
        loopBanner.setIndicator(false)
                .setImageLoader(new MyImageLoader())
                .load(list.toArray())
                .build()
                .play();

        BannerLayout loopBanner1 = findViewById(R.id.banner_loop1);
        loopBanner1.setIndicator(true)
                .setImageLoader(new MyImageLoader())
                .load(list.toArray())
                .setTransformerStyle(TransformerStyle.STYLE_NONE)
                .build();
    }
}
