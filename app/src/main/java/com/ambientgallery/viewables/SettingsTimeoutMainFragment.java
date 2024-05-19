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
    static final int CURRENT = 0, MIN = 1, MAX = 2;

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

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
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
            if (picker == hourPicker) {
                if (newVal == 24) {
                    minutePicker.setMaxValue(0);
                    secondPicker.setMaxValue(0);
                } else {
                    minutePicker.setMaxValue(59);
                    secondPicker.setMaxValue(59);
                }
            }

            int n0 = hourPicker.getValue();
            int n1 = minutePicker.getValue();
            int n2 = secondPicker.getValue();
            int n = (n0 * 60 + n1) * 60 + n2;
        };
        hourPicker.setOnValueChangedListener(onValueChangeListener);
        minutePicker.setOnValueChangedListener(onValueChangeListener);
        secondPicker.setOnValueChangedListener(onValueChangeListener);
    }
}
