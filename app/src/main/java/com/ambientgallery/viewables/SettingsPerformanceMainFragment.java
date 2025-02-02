package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.IMAGE_QUALITY_LEVEL;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.SharedPrefsUtil.setPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsPerformanceMainFragment extends Fragment {
    SharedPreferences prefs;
    SeekBar imageQualitySeek;
    TextView imageQualityValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_performance_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageQualitySeek = requireActivity().findViewById(R.id.settings_performance_main_image_quality_seekbar);
        imageQualityValue = requireActivity().findViewById(R.id.settings_performance_main_image_quality_value);
        imageQualitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageQualityValue.setText("" + progress);
                if (fromUser) {
                    setPrefs(prefs, IMAGE_QUALITY_LEVEL, progress);
                    Bundle resultBundle = new Bundle();
                    resultBundle.putInt("imageQualityLevel", progress);
                    getParentFragmentManager().setFragmentResult("imageQualityLevel", resultBundle);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imageQualitySeek.setProgress(prefsInt(prefs, IMAGE_QUALITY_LEVEL));


    }
}
