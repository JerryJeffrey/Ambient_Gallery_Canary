package com.ambientgallery.utils;

import static com.ambientgallery.components.AppStatus.inSampleLevel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {
    public static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            switch (inSampleLevel) {
                case 0: {
                    while ((height / 2 / inSampleSize) >= reqHeight
                            && (width / 2 / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                }
                case 1: {
                    while ((height / 2 / inSampleSize) >= reqHeight
                            || (width / 2 / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                }
                case 2: {
                    while ((height / inSampleSize) >= reqHeight
                            && (width / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                }
                default: {
                    while ((height / inSampleSize) >= reqHeight
                            || (width / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                    break;
                }
            }
        }
        return inSampleSize;
    }
}
