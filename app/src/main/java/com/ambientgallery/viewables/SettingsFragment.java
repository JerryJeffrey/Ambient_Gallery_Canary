package com.ambientgallery.viewables;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsFragment extends Fragment {
    public View appearanceNormal, appearanceAmbient, performance, sensitivity, timeout, nightMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appearanceNormal = view.findViewById(R.id.settings_frag_appearance_normal);
        appearanceAmbient = view.findViewById(R.id.settings_frag_appearance_ambient);
        performance = view.findViewById(R.id.settings_frag_performance);
        sensitivity = view.findViewById(R.id.settings_frag_sensitivity);
        timeout = view.findViewById(R.id.settings_frag_timeout);
        nightMode = view.findViewById(R.id.settings_frag_night);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsDetailActivity.class);
                if (getActivity() != null) {
                    intent.putExtra("currentPath",
                            getActivity().getIntent().getStringExtra("currentPath"));
                }
                intent.putExtra("viewId", v.getId());
                startActivity(intent);
            }
        };
        appearanceNormal.setOnClickListener(onClickListener);
        appearanceAmbient.setOnClickListener(onClickListener);
        performance.setOnClickListener(onClickListener);
        sensitivity.setOnClickListener(onClickListener);
        timeout.setOnClickListener(onClickListener);
        nightMode.setOnClickListener(onClickListener);
    }
}
