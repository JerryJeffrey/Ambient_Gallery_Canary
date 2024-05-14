package com.ambientgallery.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefsUtil {
    private static final int PREFS_BOOLEAN = 0, PREFS_INT = 1, PREFS_FLOAT = 2, PREFS_STRING = 3;

    public static void initPrefs(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //noinspection SwitchStatementWithTooFewBranches
        switch (name) {
            case "MainPrefs":
                //opacity
                addEdit(prefs, editor, "bgAmbientOpacity", 0.5f);
                addEdit(prefs, editor, "bgNormalOpacity", 1.0f);
                addEdit(prefs, editor, "textAmbientOpacity", 0.6f);
                addEdit(prefs, editor, "textNormalOpacity", 1.0f);
                //timeout
                addEdit(prefs, editor, "hideButtonTimeout", 5);
                addEdit(prefs, editor, "ambientTimeout", 30);
                addEdit(prefs, editor, "switchImageTimeout", 300);
                //night brightness
                addEdit(prefs, editor, "nightStartBrightness", 0f);
                addEdit(prefs, editor, "nightEndBrightness", 3f);
                //animation duration
                addEdit(prefs, editor, "animationDuration_instant", 400);
                addEdit(prefs, editor, "animationDuration_short", 800);
                addEdit(prefs, editor, "animationDuration_normal", 1600);
                addEdit(prefs, editor, "animationDuration_long", 3200);
                //switch image scale
                addEdit(prefs, editor, "switchImageScale", 1.1f);
                //touch event sensitivity
                addEdit(prefs, editor, "dragStartSensitivity", 12);
                addEdit(prefs, editor, "dragEndSensitivity", 96);
                //others
                addEdit(prefs, editor, "imageQualityLevel", 1);
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
