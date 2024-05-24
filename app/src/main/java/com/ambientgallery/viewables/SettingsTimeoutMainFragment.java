package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

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
        hourPicker = getActivity().findViewById(R.id.settings_timeout_main_picker_hour);
        minutePicker = getActivity().findViewById(R.id.settings_timeout_main_picker_minute);
        secondPicker = getActivity().findViewById(R.id.settings_timeout_main_picker_second);
        tabBar = getActivity().findViewById(R.id.settings_timeout_main_tab_container);
        setPickerByTab();
        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                setPickerByTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        NumberPicker.OnValueChangeListener onValueChangeListener = (picker, oldVal, newVal) -> {
            getView().performHapticFeedback(1);

            int currentHour = hourPicker.getValue();
            int currentMinute = minutePicker.getValue();
            int currentSecond = secondPicker.getValue();
            int currentTime = (currentHour * 60 + currentMinute) * 60 + currentSecond;
        };
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        secondPicker.setOnValueChangedListener(onValueChangeListener);
    }

    private void setPickerByTab() {
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
        setPickerRange(min, max);
        setPickerCurrent(current);

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

    private void setPickerRange(int min, int max) {
        Log.i("asd",min+","+max);
        int minHour = min / 3600;
        int minMinute = (min - minHour * 3600) / 60;
        int minSecond = min - minHour * 3600 - minMinute * 60;
        int maxHour = max / 3600;
        int maxMinute = (max - maxHour * 3600) / 60;
        int maxSecond = max - maxHour * 3600 - maxMinute * 60;
        hourPicker.setMinValue(minHour);
        hourPicker.setMaxValue(maxHour);
        if (maxHour-minHour>0){
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
        } else {
            minutePicker.setMinValue(minMinute);
            minutePicker.setMaxValue(maxMinute);
        }
        if (maxMinute-minMinute>0){
            secondPicker.setMinValue(0);
            secondPicker.setMaxValue(59);
        } else {
            secondPicker.setMinValue(minSecond);
            secondPicker.setMaxValue(maxSecond);
        }

    }
}
