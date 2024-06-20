package com.ambientgallery.viewables;

import static com.ambientgallery.utils.AnimateUtil.viewOpacity;
import static com.ambientgallery.utils.AnimateUtil.viewPosition;
import static com.ambientgallery.utils.SharedPrefsUtil.ANIMATION_DURATION_SHORT;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.viewables.MainActivity.MAIN_IMAGE_PATH;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;
import com.ambientgallery.utils.AnimateUtil;

public class MainToolbarFragment extends Fragment {
    public static final int MAIN_TOOLBAR_STATUS_NIGHT = 1;
    public static final int MAIN_TOOLBAR_STATUS_BATTERY = 2;
    public static final int MAIN_TOOLBAR_STATUS_UNCHARGED_STATE = 3;
    public static final int MAIN_TOOLBAR_STATUS_UNCHARGED_BROADCAST = 4;
    SharedPreferences prefs;
    ImageButton settingsButton, ambientButton;
    View statusBattery, statusUncharged, statusUnchargedState, statusUnchargedBroadcast, statusNight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_main_toolbar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsButton = requireActivity().findViewById(R.id.main_toolbar_button_settings);
        ambientButton = requireActivity().findViewById(R.id.main_toolbar_button_ambient);
        statusBattery = requireActivity().findViewById(R.id.main_toolbar_status_battery_container);
        statusUncharged = requireActivity().findViewById(R.id.main_toolbar_status_uncharged_container);
        statusNight = requireActivity().findViewById(R.id.main_toolbar_status_night_container);
        statusUnchargedState = requireActivity().findViewById(R.id.main_toolbar_status_uncharged_state);
        statusUnchargedBroadcast = requireActivity().findViewById(R.id.main_toolbar_status_uncharged_broadcast);

        for (View v : new View[]{statusNight, statusBattery, statusUncharged, statusUnchargedState, statusUnchargedBroadcast}) {
            v.setVisibility(View.GONE);
            v.setTranslationY(-12);
            v.setAlpha(0);
        }

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(requireContext(),SettingsActivity.class);

            }
        });
        ambientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setStatus(int status, boolean state) {
        View[] views = new View[]{};
        //init as false
        int visibility = View.GONE;
        float y = -12, alpha = 0;
        if (state) {
            y = 0;
            alpha = 1;
            visibility = View.VISIBLE;
        }
        switch (status) {
            case MAIN_TOOLBAR_STATUS_NIGHT:
                views = new View[]{statusNight};
                break;
            case MAIN_TOOLBAR_STATUS_BATTERY:
                views = new View[]{statusBattery};
                break;
            case MAIN_TOOLBAR_STATUS_UNCHARGED_STATE:
                views = new View[]{statusUncharged, statusUnchargedState};
                break;
            case MAIN_TOOLBAR_STATUS_UNCHARGED_BROADCAST:
                views = new View[]{statusUncharged, statusUnchargedBroadcast};
                break;
            default:
                break;
        }
        for (View v : views) v.setVisibility(visibility);
        viewPosition(views, 0, y, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
        viewOpacity(views, alpha, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
    }
}
