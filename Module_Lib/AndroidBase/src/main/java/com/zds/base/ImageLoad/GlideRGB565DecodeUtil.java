package com.zds.base.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.security.MessageDigest;

public class GlideRGB565DecodeUtil {
    private static Field bmpInBitmapResource;
    private static Field bmpPoolInBitmapResource;

    public static RequestOptions getRequestOption() {
        Transformation<Bitmap> transformation = new Transformation<Bitmap>() {
            @NonNull
            @Override
            public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
                Bitmap bitmap = resource.get();
                if (null == bitmap)
                    return resource;

                if (bitmap.getConfig() == Bitmap.Config.RGB_565) {
                    // 本身就是RGB_565的
                    return resource;
                }
                try {
                    if (null == bmpPoolInBitmapResource) {
                        bmpPoolInBitmapResource = BitmapResource.class.getDeclaredField("bitmapPool");
                        bmpPoolInBitmapResource.setAccessible(true);
                    }

                    if (null == bmpInBitmapResource) {
                        bmpInBitmapResource = BitmapResource.class.getDeclaredField("bitmap");
                        bmpInBitmapResource.setAccessible(true);
                    }

                    // 1.通过原图生成565的图
                    BitmapPool bitmapPool = (BitmapPool) bmpPoolInBitmapResource.get(resource);
                    Bitmap bitmap565 = bitmapPool.get(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap565);
                    // 设置背景色，防止透明图出现黑色的情况
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    // 2.使用565的图替换原图
                    bmpInBitmapResource.set(resource, bitmap565);

                    bitmapPool.put(bitmap);

                } catch (Exception ignore) {
                }
                return resource;
            }

            @Override
            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            }
        };
        Transformation<Bitmap> gifTransformation = new Transformation<Bitmap>() {
            @NonNull
            @Override
            public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
                return resource;
            }

            @Override
            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            }
        };
        DrawableTransformation drawableTransformation =
                new DrawableTransformation(transformation, true);
        return new RequestOptions().transform(Bitmap.class, transformation)
                .transform(Drawable.class, drawableTransformation)
                .transform(BitmapDrawable.class, drawableTransformation.asBitmapDrawable())
                // GIF不能用RGB565加载，否则会出现背景白色问题
                .transform(GifDrawable.class, new GifDrawableTransformation(gifTransformation))
                .format(DecodeFormat.PREFER_RGB_565);
    }

    public static RequestOptions getARGBRequestOption() {
        return new RequestOptions().dontTransform()
                .format(DecodeFormat.PREFER_ARGB_8888);
    }
}