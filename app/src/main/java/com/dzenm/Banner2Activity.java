package com.dzenm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.banner2.IndicatorView;
import com.dzenm.banner2.PagerLayout;

import java.util.ArrayList;
import java.util.List;

public class Banner2Activity extends AppCompatActivity implements View.OnClickListener {

    private IndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner2);

        final PagerLayout pl_gallery = findViewById(R.id.pl_gallery);
        indicatorView = findViewById(R.id.custom_indicator);
        Button btn1 = findViewById(R.id.btn_type1);
        Button btn2 = findViewById(R.id.btn_type2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        final List<Integer> images = new ArrayList<>();
        images.add(R.drawable.one);
        images.add(R.drawable.three);
        images.add(R.drawable.four);
        images.add(R.drawable.two);
        images.add(R.drawable.five);
        images.add(R.drawable.six);
        images.add(R.drawable.seven);
        pl_gallery.load(images)
                .setItemViewMargin(0)
                .gallery()
                .setTransformerStyle(com.dzenm.banner2.impl.TransformerStyle.STYLE_3D)
                .into(new MyTestImageLoader())
                .build();
        indicatorView.bindViewPager(pl_gallery.getViewPager());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_type1) {
            indicatorView.setIndicatorScrollType(IndicatorView.IndicatorScroll.SCALE);
        } else if (v.getId() == R.id.btn_type2) {
            indicatorView.setIndicatorScrollType(IndicatorView.IndicatorScroll.SPLIT);
        }
    }
}
