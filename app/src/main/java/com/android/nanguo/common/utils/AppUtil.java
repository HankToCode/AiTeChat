package com.android.nanguo.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;


/**
 * Created by zzs on 2017/8/29.
 */

public class AppUtil {
    /**
     * 兼容性的获取颜色信息
     *
     * @param context
     * @param id
     * @return
     */
    public static int getColorId(Context context, int id) {
        Resources resources = context.getResources();
        Resources.Theme theme = context.getTheme();
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = resources.getColor(id, theme);
        } else {
            color = resources.getColor(id);
        }
        return color;
    }

    /**
     * dp转换为px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + .5f);
    }

}
