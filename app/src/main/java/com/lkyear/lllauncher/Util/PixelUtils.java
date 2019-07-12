package com.lkyear.lllauncher.Util;

import android.content.Context;

/**
 * 像素工具类
 * @author lkyear
 */

public class PixelUtils {

    /**
     * 像素转DP
     * @param context 上下文
     * @param pixel 像素值
     * @return 像素转DP值
     */
    public static int px2dp(Context context, float pixel) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixel / scale + 0.5f);
    }

    /**
     * DP转像素
     * @param context 上下文
     * @param dp DP值
     * @return DP转像素值
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
