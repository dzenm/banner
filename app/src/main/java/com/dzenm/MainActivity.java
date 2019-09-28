package com.dzenm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView banner = findViewById(R.id.tv_banner);
        TextView banner2 = findViewById(R.id.tv_banner2);
        banner.setOnClickListener(this);
        banner2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_banner) {
            Intent intent = new Intent(this, BannerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tv_banner2) {
            Intent intent = new Intent(this, Banner2Activity.class);
            startActivity(intent);
        }
    }
}
