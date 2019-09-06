package com.dzenm;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dzenm.banner.ImageLoader;

/**
 * @author dzenm
 * @date 2019-09-06 14:50
 */
public class MyImageLoader implements ImageLoader {

    @Override
    public void onLoader(View view, Object imageResource) {
        RoundedCorners rc = new RoundedCorners(20);
        RequestOptions options = RequestOptions.bitmapTransform(rc);
        Glide.with(view.getContext()).load(imageResource).apply(options).into((ImageView) view);
    }
}
