package com.ycf.qianzhihe.common.widget;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Integer p = (Integer) path;
        imageView.setImageResource(p);
//            Glide.with(context).load((String)path).into(imageView);
    }
}
