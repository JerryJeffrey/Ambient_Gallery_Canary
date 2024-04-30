package com.ambientgallery.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import static com.ambientgallery.components.AppStatus.*;

public class DimensUtil {
    public static float dp2px(Context context,float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }
    public static float px2dp(Context context,float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px / scale;
    }
    public static void getDisplayMetrics(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
            displayWidth = metrics.widthPixels;
            displayHeight = metrics.heightPixels;
        } else {
            displayWidth = windowManager.getDefaultDisplay().getWidth();
            displayHeight = windowManager.getDefaultDisplay().getHeight();
        }
    }
}
