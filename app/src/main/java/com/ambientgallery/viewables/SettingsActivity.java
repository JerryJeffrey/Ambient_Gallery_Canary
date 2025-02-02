package com.ambientgallery.viewables;

import static com.ambientgallery.utils.DimensUtil.dp2px;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.WindowFeatureUtil.allowCutoutDisplay;
import static com.ambientgallery.utils.WindowFeatureUtil.goImmersive;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ambientgallery.R;
import com.ambientgallery.components.DisplayDimensions;

public class SettingsActivity extends AppCompatActivity {
    View subSplit;
    ImageButton backButton;
    Window window;
    WindowManager windowManager;
    Fragment settingsFragment;
    FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        window = getWindow();
        windowManager = getWindowManager();
        subSplit = findViewById(R.id.settings_subsplit);
        backButton = findViewById(R.id.settings_button_back);
        getDisplayMetrics(windowManager);
        settingsFragment = new SettingsFragment();
        fragmentManager = getSupportFragmentManager();
        allowCutoutDisplay(window);
        //actions on back button
        backButton.setOnClickListener(view -> finish());
    }


    @Override
    protected void onResume() {
        super.onResume();
        DisplayDimensions dimensions = getDisplayMetrics(getWindowManager());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //if is landscape and long edge larger than 640dp
        if ((float) dimensions.width / dimensions.height >= 1 && dimensions.width >= dp2px(getApplicationContext(), 640)) {
            transaction.add(R.id.settings_sub_attach_point, settingsFragment);
            subSplit.setVisibility(View.VISIBLE);
        } else {
            transaction.add(R.id.settings_main_attach_point, settingsFragment);
            subSplit.setVisibility(View.GONE);
        }
        transaction.commit();
        goImmersive(window);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragmentManager.beginTransaction().remove(settingsFragment).commit();
    }
}

