package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.SharedPrefsUtil.setPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;
import com.google.android.material.tabs.TabLayout;

public class SettingsTimeoutMainFragment extends Fragment {
    NumberPicker hourPicker, minutePicker, secondPicker;
    SharedPreferences prefs;
    TabLayout tabBar;
    int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            prefs = getActivity().getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_timeout_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            hourPicker = getActivity().findViewById(R.id.settings_timeout_main_picker_hour);
            minutePicker = getActivity().findViewById(R.id.settings_timeout_main_picker_minute);
            secondPicker = getActivity().findViewById(R.id.settings_timeout_main_picker_second);
            tabBar = getActivity().findViewById(R.id.settings_timeout_main_tab_container);
        }
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
            if (getView() != null) getView().performHapticFeedback(1);
            setPickerByTab(false);
            updatePrefsByTab();

        };
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        secondPicker.setOnValueChangedListener(onValueChangeListener);
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
        int min = 1, max = 86400, current = 1;
        switch (currentTab) {
            case 0://hide buttons
                max = prefsInt(prefs, "ambientTimeout");
                current = prefsInt(prefs, "hideButtonTimeout");
                break;
            case 1://go ambient
                min = prefsInt(prefs, "hideButtonTimeout");
                max = prefsInt(prefs, "switchImageTimeout");
                current = prefsInt(prefs, "ambientTimeout");
                break;
            case 2://switch background
                min = prefsInt(prefs, "ambientTimeout");
                current = prefsInt(prefs, "switchImageTimeout");
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
                setPrefs(prefs, "hideButtonTimeout", getPickerCurrent());
                break;
            case 1://go ambient
                setPrefs(prefs, "ambientTimeout", getPickerCurrent());
                break;
            case 2://switch background
                setPrefs(prefs, "switchImageTimeout", getPickerCurrent());
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
        int currentSecond = current - currentHour * 3600 - currentMinute * 60;
        Log.i("asd",minHour+":"+minMinute+":"+minSecond);
        Log.i("asd",maxHour+":"+maxMinute+":"+maxSecond);
        Log.i("asd",currentHour+":"+currentMinute+":"+currentSecond);
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
}
