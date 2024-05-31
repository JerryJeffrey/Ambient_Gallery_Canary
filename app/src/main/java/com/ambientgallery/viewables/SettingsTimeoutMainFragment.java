package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.GO_AMBIENT_TIMEOUT;
import static com.ambientgallery.utils.SharedPrefsUtil.HIDE_BUTTON_TIMEOUT;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.SWITCH_IMAGE_TIMEOUT;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.SharedPrefsUtil.setPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SettingsTimeoutMainFragment extends Fragment {
    NumberPicker hourPicker, minutePicker, secondPicker;
    SharedPreferences prefs;
    TabLayout tabBar;
    int currentTab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_timeout_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        hourPicker = requireActivity().findViewById(R.id.settings_timeout_main_picker_hour);
        minutePicker = requireActivity().findViewById(R.id.settings_timeout_main_picker_minute);
        secondPicker = requireActivity().findViewById(R.id.settings_timeout_main_picker_second);
        tabBar = requireActivity().findViewById(R.id.settings_timeout_main_tab_container);

        fixPicker(hourPicker);
        fixPicker(minutePicker);
        fixPicker(secondPicker);
        currentTab = tabBar.getSelectedTabPosition();
        setPickerByTab(true);
        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                setPickerByTab(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        NumberPicker.OnValueChangeListener onValueChangeListener = (picker, oldVal, newVal) -> {
            requireView().performHapticFeedback(1);
            setPickerByTab(false);
            updatePrefsByTab();

        };
        View.OnClickListener onClickListener = v -> fixPicker((NumberPicker) v);
        View.OnLongClickListener onLongClickListener = v -> {
            v.performClick();
            return true;
        };
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        secondPicker.setOnValueChangedListener(onValueChangeListener);
        hourPicker.setOnClickListener(onClickListener);
        minutePicker.setOnClickListener(onClickListener);
        secondPicker.setOnClickListener(onClickListener);
        hourPicker.setOnLongClickListener(onLongClickListener);
        minutePicker.setOnLongClickListener(onLongClickListener);
        secondPicker.setOnLongClickListener(onLongClickListener);
    }

    private int getPickerCurrent() {
        return (hourPicker.getValue() * 60 + minutePicker.getValue()) * 60 + secondPicker.getValue();
    }

    private void setPickerCurrent(int current) {
        int currentHour = current / 3600;
        int currentMinute = (current - currentHour * 3600) / 60;
        int currentSecond = current - currentHour * 3600 - currentMinute * 60;
        hourPicker.setValue(currentHour);
        minutePicker.setValue(currentMinute);
        secondPicker.setValue(currentSecond);
    }

    private void setPickerByTab(boolean setCurrent) {
        int min = 3, max = 86400, current = 1;
        switch (currentTab) {
            case 0://hide buttons
                max = prefsInt(prefs, GO_AMBIENT_TIMEOUT);
                current = prefsInt(prefs, HIDE_BUTTON_TIMEOUT);
                break;
            case 1://go ambient
                min = prefsInt(prefs, HIDE_BUTTON_TIMEOUT);
                max = prefsInt(prefs, SWITCH_IMAGE_TIMEOUT);
                current = prefsInt(prefs, GO_AMBIENT_TIMEOUT);
                break;
            case 2://switch background
                min = prefsInt(prefs, GO_AMBIENT_TIMEOUT);
                current = prefsInt(prefs, SWITCH_IMAGE_TIMEOUT);
                break;
        }
        if (setCurrent) {
            setPickerRange(min, max, current);
            setPickerCurrent(current);
        } else {
            setPickerRange(min, max, getPickerCurrent());
        }

    }

    private void updatePrefsByTab() {
        switch (currentTab) {
            case 0://hide buttons
                setPrefs(prefs, HIDE_BUTTON_TIMEOUT, getPickerCurrent());
                break;
            case 1://go ambient
                setPrefs(prefs, GO_AMBIENT_TIMEOUT, getPickerCurrent());
                break;
            case 2://switch background
                setPrefs(prefs, SWITCH_IMAGE_TIMEOUT, getPickerCurrent());
                break;
        }
    }

    private void setPickerRange(int min, int max, int current) {
        int minHour = min / 3600;
        int minMinute = (min - minHour * 3600) / 60;
        int minSecond = min - minHour * 3600 - minMinute * 60;
        int maxHour = max / 3600;
        int maxMinute = (max - maxHour * 3600) / 60;
        int maxSecond = max - maxHour * 3600 - maxMinute * 60;
        int currentHour = current / 3600;
        int currentMinute = (current - currentHour * 3600) / 60;
        hourPicker.setMinValue(minHour);
        hourPicker.setMaxValue(maxHour);
        if (currentHour <= minHour) {
            minutePicker.setMinValue(minMinute);
            if (currentMinute <= minMinute) {
                secondPicker.setMinValue(minSecond);
            } else {
                secondPicker.setMinValue(0);
            }
        } else {
            minutePicker.setMinValue(0);
            secondPicker.setMinValue(0);
        }
        if (currentHour >= maxHour) {
            minutePicker.setMaxValue(maxMinute);
            if (currentMinute >= maxMinute) {
                secondPicker.setMaxValue(maxSecond);
            } else {
                secondPicker.setMaxValue(59);
            }
        } else {
            minutePicker.setMaxValue(59);
            secondPicker.setMaxValue(59);
        }
    }

    private void fixPicker(NumberPicker picker) {
        try {
            @SuppressLint("DiscouragedPrivateApi") Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(picker, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
