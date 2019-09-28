package com.dzenm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dzenm.banner.BannerLayout;
import com.dzenm.banner.TransformerStyle;

import java.util.ArrayList;
import java.util.List;

public class BannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

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
//                .play(2);
    }
}
