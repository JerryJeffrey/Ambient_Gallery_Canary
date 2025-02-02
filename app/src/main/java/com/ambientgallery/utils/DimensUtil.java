/**
* This class offers:
* Methods that implements conversion between pixel & dip by getting Context and px/dp.
* A method returns image fill scale to offer unified scaling over app with display, image and view size. A fullscreen ImageView with scale fit is referred as 100%.
*/
package com.ambientgallery.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.ambientgallery.components.DisplayDimensions;

public class DimensUtil {

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    public static float px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px / scale;
    }

    public static DisplayDimensions getDisplayMetrics(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        DisplayDimensions dimensions = new DisplayDimensions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
            dimensions.width = metrics.widthPixels;
            dimensions.height = metrics.heightPixels;
        } else {
            dimensions.width = windowManager.getDefaultDisplay().getWidth();
            dimensions.height = windowManager.getDefaultDisplay().getHeight();
        }
        return dimensions;
    }

    private static float getFillScale(int screenWidth, int screenHeight, int imageWidth, int imageHeight) {
        float widthScale = (float) screenWidth / imageWidth,
                heightScale = (float) screenHeight / imageHeight;
        return Math.max(widthScale, heightScale);
    }

    public static float getPartialImageScale(DisplayDimensions dimensions, int imageWidth, int imageHeight, int layoutWidth, int layoutHeight) {
        int screenWidth=dimensions.width,screenHeight=dimensions.height;
        return getFillScale(screenWidth, screenHeight, imageWidth, imageHeight) /
                getFillScale(layoutWidth, layoutHeight, imageWidth, imageHeight);
    }
}
