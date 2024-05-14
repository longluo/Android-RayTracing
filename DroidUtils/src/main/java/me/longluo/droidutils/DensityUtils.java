package me.longluo.droidutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DensityUtils {

    public static String getDisplaySpec(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        // 屏幕密度（1.0 / 1.5 / 2.0）
        float density = metrics.density;
        // 屏幕密度DPI（160 / 240 / 320）
        int dpi = metrics.densityDpi;

        float scaledDensity = metrics.scaledDensity;

        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;

        // density = 1.5, dpi = 240

        int smallWidth = Math.min(widthPixels, heightPixels);

        double swDp = (double) smallWidth * 160 / dpi;

        StringBuilder sb = new StringBuilder();
        sb.append("size: ").append(widthPixels).append("x").append(heightPixels)
                .append(", smallWidth:").append(smallWidth).append(", swdp:").append(swDp);
        sb.append(", density:").append(density).append(", dpi:").append(dpi).
                append(", scaledDensity:").append(scaledDensity);

        return sb.toString();
    }

    /**
     * dp值转换成px值
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, final float dpValue) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px值转换成dp值
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context, final float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp值转换成px值
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context, final float spValue) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px值转换成sp值
     *
     * @param pxVal px值
     * @return sp值
     */
    public static int px2sp(Context context, final float pxVal) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxVal / fontScale + 0.5f);
    }

    public static Point getDisplayPoint(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;

        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        return new Point(dm.widthPixels, dm.heightPixels);
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return wm.getCurrentWindowMetrics().getBounds().width();
        } else {
            return wm.getDefaultDisplay().getWidth();
        }
    }
}
