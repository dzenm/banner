package com.dzenm;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dzenm.banner2.impl.ImageLoader;

/**
 * @author dzenm
 * @date 2019-09-11 08:44
 */
public class MyImageLoader2 implements ImageLoader {
    @Override
    public void onLoader(ImageView view, Object imageResource) {
        RoundedCorners rc = new RoundedCorners(20);
        RequestOptions options = RequestOptions.bitmapTransform(rc);
        Glide.with(view.getContext()).load(imageResource).apply(options).into(view);
    }
}
