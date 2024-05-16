package com.ambientgallery.viewables;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsTimeoutMainFragment extends Fragment {
    NumberPicker numberPicker0, numberPicker1, numberPicker2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_timeout_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        numberPicker0 = getActivity().findViewById(R.id.numberPicker000);
        numberPicker1 = getActivity().findViewById(R.id.numberPicker111);
        numberPicker2 = getActivity().findViewById(R.id.numberPicker222);
        setPickerValue(numberPicker0, 0, 24, 0);
        setPickerValue(numberPicker1, 0, 59, 0);
        setPickerValue(numberPicker2, 0, 59, 0);

        NumberPicker.OnValueChangeListener onValueChangeListener = (picker, oldVal, newVal) -> {
            getView().performHapticFeedback(1);
            if (picker == numberPicker0) {
                if (newVal == 24) {
                    numberPicker1.setMaxValue(0);
                    numberPicker2.setMaxValue(0);
                } else {
                    numberPicker1.setMaxValue(59);
                    numberPicker2.setMaxValue(59);
                }
            }

            int n0 = numberPicker0.getValue();
            int n1 = numberPicker1.getValue();
            int n2 = numberPicker2.getValue();
            int n = (n0 * 60 + n1) * 60 + n2;
        };
        numberPicker0.setOnValueChangedListener(onValueChangeListener);
        numberPicker1.setOnValueChangedListener(onValueChangeListener);
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
    }

    private void setPickerValue(NumberPicker picker, int min, int max, int current) {
        picker.setMaxValue(max);
        picker.setMinValue(min);
        picker.setValue(current);
    }
}
