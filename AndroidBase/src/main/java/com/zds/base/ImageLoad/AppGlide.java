package com.zds.base.ImageLoad;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

public class AppGlide extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
            builder.setDefaultRequestOptions(GlideRGB565DecodeUtil.getRequestOption());
    }
}
