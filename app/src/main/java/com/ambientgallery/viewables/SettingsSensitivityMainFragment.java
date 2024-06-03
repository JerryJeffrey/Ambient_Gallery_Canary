package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.DRAG_END_SENSITIVITY;
import static com.ambientgallery.utils.SharedPrefsUtil.DRAG_START_SENSITIVITY;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsSensitivityMainFragment extends Fragment {
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_sensitivity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateCardValue(0, prefsInt(prefs, DRAG_START_SENSITIVITY), prefsInt(prefs, DRAG_END_SENSITIVITY));
    }

    private void updateCardValue(int start, int middle, int end) {
        //give info to card
        Bundle resultBundle = new Bundle();
        resultBundle.putInt("start", start);
        resultBundle.putInt("middle", middle);
        resultBundle.putInt("end", end);
        getParentFragmentManager().setFragmentResult("indicatorValues", resultBundle);
    }
}
