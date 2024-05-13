package com.ambientgallery.viewables;

import static com.ambientgallery.R.*;
import static com.ambientgallery.R.id.*;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.WindowFeatureUtil.goImmersive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ambientgallery.R;
import com.ambientgallery.components.DisplayDimensions;

public class SettingsDetailActivity extends AppCompatActivity {
    View mainSplit, subSplit, card, backButton;
    Intent intent;
    Window window;
    WindowManager windowManager;
    Fragment targetMainFragment, targetCardFragment;
    FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_settings_detail);
        window = getWindow();
        windowManager = getWindowManager();
        intent = getIntent();
        mainSplit = findViewById(settings_detail_main_attach_point);
        subSplit = findViewById(settings_detail_subsplit);
        card = findViewById(settings_detail_card_attach_point);
        backButton = findViewById(settings_detail_button_back_icon);
        getDisplayMetrics(windowManager);
        fragmentManager = getSupportFragmentManager();

        backButton.setOnClickListener(view -> {
            finish();
        });
        backButton.setOnLongClickListener(view -> {
            Toast.makeText(SettingsDetailActivity.this, getText(string.button_back),
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        int viewId = intent.getIntExtra("viewId", 0);
        if (viewId == 0) {
            Toast.makeText(this, "Submenu not found", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.settings_frag_appearance) {
            targetMainFragment = new SettingsBrightnessMainFragment();
            targetCardFragment = new SettingsBrightnessCardFragment();
        } else if (viewId == settings_frag_performance) {
            targetMainFragment = new SettingsPerformanceMainFragment();
            targetCardFragment = new SettingsPerformanceCardFragment();
        } else if (viewId == settings_frag_sensitivity) {
            targetMainFragment = new SettingsSensitivityMainFragment();
            targetCardFragment = new SettingsSensitivityCardFragment();
        } else if (viewId == settings_frag_timeout) {
            targetMainFragment = new SettingsTimeoutMainFragment();
            targetCardFragment = new SettingsTimeoutCardFragment();
        } else if (viewId == settings_frag_night) {
            targetMainFragment = new SettingsNightMainFragment();
            targetCardFragment = new SettingsNightCardFragment();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        DisplayDimensions dimensions = getDisplayMetrics(getWindowManager());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(settings_detail_card_attach_point, targetCardFragment);
        //if is landscape
        if ((float) dimensions.width / dimensions.height >= 1) {
            transaction.add(subSplit.getId(), targetMainFragment);
            mainSplit.setVisibility(View.GONE);
            subSplit.setVisibility(View.VISIBLE);

        } else {
            transaction.add(mainSplit.getId(), targetMainFragment);
            mainSplit.setVisibility(View.VISIBLE);
            subSplit.setVisibility(View.GONE);
        }
        transaction.commit();
        goImmersive(window);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragmentManager.beginTransaction()
                .remove(targetMainFragment).remove(targetCardFragment).commit();
    }
}
