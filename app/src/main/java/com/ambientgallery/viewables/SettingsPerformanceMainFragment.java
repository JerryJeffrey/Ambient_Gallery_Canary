package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.SharedPrefsUtil.setPrefs;

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
        if (getActivity() != null)
            prefs = getActivity().getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_performance_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity()!=null){
            imageQualitySeek=getActivity().findViewById(R.id.settings_performance_main_image_quality_seekbar);
            imageQualityValue=getActivity().findViewById(R.id.settings_performance_main_image_quality_value);
            imageQualitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    imageQualityValue.setText(""+progress);
                    if (fromUser){
                        setPrefs(prefs, "imageQualityLevel", progress);
                        Bundle resultBundle=new Bundle();
                        resultBundle.putInt("imageQualityLevel",progress);
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
            imageQualitySeek.setProgress(prefsInt(prefs, "imageQualityLevel"));
        }


    }
}
