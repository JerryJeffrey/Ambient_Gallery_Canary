package com.ambientgallery.components;

import java.util.ArrayList;

public class AppStatus {
    public static int currentTime = 0;
    public static float currentBrightness =0;
    public static float bgAmbientOpacity =0.4f;
    public static final float bgNormalOpacity =0.8f;
    public static float textAmbientOpacity =0.6f;
    public static final float textNormalOpacity =0.9f;
    public static int hideButtonTimeout =5;
    public static int ambientTimeout = 30;
    public static int switchImageTimeout = 60;
    public static int nightBrightness = 0;
    public static boolean upperImgVisible = false;
    public static final int animationDuration_instant = 400;
    public static final int animationDuration_short = 800;
    public static final int animationDuration_normal = 1600;
    public static final int animationDuration_long = 3200;
    public static int inSampleLevel =1;
    public static int imageListIndex=0;
    public static int dragStartSensitivity =12;
    public static int dragEndSensitivity =96;
    public static int displayWidth, displayHeight;
    public static String[] fileFormat={".jpg",".jpeg",".png",".bmp",".webp"};
    public static ArrayList<String> fileList = new ArrayList<>();
    public static void updateTime() {
        if (currentTime < ambientTimeout || currentTime < switchImageTimeout) {
            currentTime += 1;
        }
    }
}
