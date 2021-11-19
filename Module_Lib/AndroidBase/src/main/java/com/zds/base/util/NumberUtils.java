package com.zds.base.util;

import android.util.Log;

/**
 * @author SpannerBear
 * @date 2020/11/13
 * Description:
 */
public class NumberUtils {
    private static final String TAG = "NumberUtils";

    public static int parseInt(String num) {
        try {
            return !StringUtil.isEmpty(num) ? Integer.parseInt(num) : 0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "parserInt: ", e);
            return 0;
        }
    }

    public static float parseFloat(String num) {
        try {
            return !StringUtil.isEmpty(num) ? Float.parseFloat(num) : 0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseFloat: ", e);
            return 0;
        }
    }

    public static double parseDouble(String num) {
        try {
            return !StringUtil.isEmpty(num) ? Double.parseDouble(num) : 0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseDouble: ", e);
            return 0;
        }
    }

    public static long parseLong(String num) {
        try {
            return !StringUtil.isEmpty(num) ? Long.parseLong(num) : 0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseLong: ", e);
            return 0;
        }
    }
}