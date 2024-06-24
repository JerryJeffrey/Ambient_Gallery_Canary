/**
 * This class offers:
 * Downscaled bitmap by giving path, required size and image quality level.
 */
package com.ambientgallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {
    public static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight, int imageQualityLevel) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight, imageQualityLevel);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int imageQualityLevel) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            switch (imageQualityLevel) {
                case -1:
                    break;
                case 0:
                    while ((height / inSampleSize) >= reqHeight
                            || (width / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                case 1:
                    while ((height / inSampleSize) >= reqHeight
                            && (width / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                case 2:
                    while ((height / 2 / inSampleSize) >= reqHeight
                            || (width / 2 / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                case 3:
                    while ((height / 2 / inSampleSize) >= reqHeight
                            && (width / 2 / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
            }
        }
        return inSampleSize;
    }
}
