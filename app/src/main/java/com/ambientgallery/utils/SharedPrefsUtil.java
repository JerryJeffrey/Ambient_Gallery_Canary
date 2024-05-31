package com.ambientgallery.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefsUtil {
    private static final int PREFS_BOOLEAN = 0, PREFS_INT = 1, PREFS_FLOAT = 2, PREFS_STRING = 3;
    public static final String MAIN_PREFS="mainPrefs";
    public static final String BG_AMBIENT_OPACITY="bgAmbientOpacity";
    public static final String BG_NORMAL_OPACITY="bgNormalOpacity";
    public static final String TEXT_MAIN_AMBIENT_OPACITY="textMainAmbientOpacity";
    public static final String TEXT_MAIN_NORMAL_OPACITY="textMainNormalOpacity";
    public static final String TEXT_SUB_AMBIENT_OPACITY="textSubAmbientOpacity";
    public static final String TEXT_SUB_NORMAL_OPACITY="textSubNormalOpacity";
    public static final String HIDE_BUTTON_TIMEOUT="hideButtonTimeout";
    public static final String GO_AMBIENT_TIMEOUT="goAmbientTimeout";
    public static final String SWITCH_IMAGE_TIMEOUT="switchImageTimeout";
    public static final String ANIMATION_DURATION_INSTANT="animationDurationInstant";
    public static final String ANIMATION_DURATION_SHORT="animationDurationShort";
    public static final String ANIMATION_DURATION_NORMAL="animationDurationNormal";
    public static final String ANIMATION_DURATION_LONG="animationDurationLong";
    public static final String NIGHT_START_BRIGHTNESS="nightStartBrightness";
    public static final String NIGHT_END_BRIGHTNESS="nightEndBrightness";
    public static final String DRAG_START_SENSITIVITY="dragStartSensitivity";
    public static final String DRAG_END_SENSITIVITY="dragEndSensitivity";
    public static final String SWITCH_IMAGE_SCALE="switchImageScale";
    public static final String IMAGE_QUALITY_LEVEL="imageQualityLevel";




    public static void initPrefs(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //noinspection SwitchStatementWithTooFewBranches
        switch (name) {
            case MAIN_PREFS:
                //opacity
                addEdit(prefs, editor, BG_AMBIENT_OPACITY, 0.5f);
                addEdit(prefs, editor, BG_NORMAL_OPACITY, 1.0f);
                addEdit(prefs, editor, TEXT_MAIN_AMBIENT_OPACITY, 0.6f);
                addEdit(prefs, editor, TEXT_MAIN_NORMAL_OPACITY, 1.0f);
                addEdit(prefs, editor, TEXT_SUB_AMBIENT_OPACITY, 0.0f);
                addEdit(prefs, editor, TEXT_SUB_NORMAL_OPACITY, 1.0f);
                //timeout
                addEdit(prefs, editor, HIDE_BUTTON_TIMEOUT, 5);
                addEdit(prefs, editor, GO_AMBIENT_TIMEOUT, 30);
                addEdit(prefs, editor, SWITCH_IMAGE_TIMEOUT, 300);
                //night brightness
                addEdit(prefs, editor, NIGHT_START_BRIGHTNESS, 0f);
                addEdit(prefs, editor, NIGHT_END_BRIGHTNESS, 3f);
                //animation duration
                addEdit(prefs, editor, ANIMATION_DURATION_INSTANT, 400);
                addEdit(prefs, editor, ANIMATION_DURATION_SHORT, 800);
                addEdit(prefs, editor, ANIMATION_DURATION_NORMAL, 1600);
                addEdit(prefs, editor, ANIMATION_DURATION_LONG, 3200);
                //touch event sensitivity
                addEdit(prefs, editor, DRAG_START_SENSITIVITY, 12);
                addEdit(prefs, editor, DRAG_END_SENSITIVITY, 96);
                //switch image scale
                addEdit(prefs, editor, SWITCH_IMAGE_SCALE, 1.1f);
                //others
                addEdit(prefs, editor, IMAGE_QUALITY_LEVEL, 1);
                break;
            default:
                break;
        }
        editor.apply();
    }

    public static boolean prefsBoolean(SharedPreferences prefs, String key) {
        if (!prefs.contains(key)) throw new RuntimeException();
        return prefs.getBoolean(key, false);
    }

    public static int prefsInt(SharedPreferences prefs, String key) {
        if (!prefs.contains(key)) throw new RuntimeException();
        return prefs.getInt(key, 0);
    }

    public static float prefsFloat(SharedPreferences prefs, String key) {
        if (!prefs.contains(key)) throw new RuntimeException();
        return prefs.getFloat(key, 0f);
    }

    public static String prefsString(SharedPreferences prefs, String key) {
        if (!prefs.contains(key)) throw new RuntimeException();
        return prefs.getString(key, "");
    }

    public static void setPrefs(SharedPreferences prefs, String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        checkPrefs(prefs,key,PREFS_BOOLEAN);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setPrefs(SharedPreferences prefs, String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        checkPrefs(prefs,key,PREFS_INT);
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setPrefs(SharedPreferences prefs, String key, float value) {
        SharedPreferences.Editor editor = prefs.edit();
        checkPrefs(prefs,key,PREFS_FLOAT);
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void setPrefs(SharedPreferences prefs, String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        checkPrefs(prefs,key,PREFS_STRING);
        editor.putString(key, value);
        editor.apply();
    }

    private static void addEdit(SharedPreferences prefs, SharedPreferences.Editor editor, String key, boolean value) {
        checkPrefs(prefs, key, PREFS_BOOLEAN);
        if (!prefs.contains(key)) {
            editor.putBoolean(key, value);
        }
    }

    private static void addEdit(SharedPreferences prefs, SharedPreferences.Editor editor, String key, int value) {
        checkPrefs(prefs, key, PREFS_INT);
        if (!prefs.contains(key)) {
            editor.putInt(key, value);
        }
    }

    private static void addEdit(SharedPreferences prefs, SharedPreferences.Editor editor, String key, float value) {
        checkPrefs(prefs, key, PREFS_FLOAT);
        if (!prefs.contains(key)) {
            editor.putFloat(key, value);
        }
    }

    private static void addEdit(SharedPreferences prefs, SharedPreferences.Editor editor, String key, String value) {
        checkPrefs(prefs, key, PREFS_STRING);
        if (!prefs.contains(key)) {
            editor.putString(key, value);
        }
    }

    private static void checkPrefs(SharedPreferences prefs, String key, int type) {
        try {
            switch (type) {
                case PREFS_BOOLEAN:
                    prefs.getBoolean(key, false);
                    break;
                case PREFS_INT:
                    prefs.getInt(key, 0);
                    break;
                case PREFS_FLOAT:
                    prefs.getFloat(key, 0);
                    break;
                case PREFS_STRING:
                    prefs.getString(key, "");
                    break;
            }
        } catch (ClassCastException e) {
            prefs.edit().remove(key).apply();
        }
    }
}
