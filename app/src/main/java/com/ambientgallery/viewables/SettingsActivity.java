package com.ambientgallery.viewables;

import static com.ambientgallery.components.AppStatus.displayHeight;
import static com.ambientgallery.components.AppStatus.displayWidth;
import static com.ambientgallery.utils.WindowFeatureUtil.goImmersive;
import static com.ambientgallery.utils.DimensUtil.*;

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

public class SettingsActivity extends AppCompatActivity {
    View subSplit,backButton;
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
        subSplit=findViewById(R.id.settings_subsplit);
        backButton=findViewById(R.id.settings_button_back);
        getDisplayMetrics(windowManager);
        settingsFragment=new SettingsFragment();
        fragmentManager=getSupportFragmentManager();

        backButton.setOnClickListener(view -> {
            finish();
        });
        backButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(SettingsActivity.this, "Back", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        if ((float) displayWidth / displayHeight >= 1) {
            transaction.add(R.id.settings_sub_attach_point,settingsFragment);
            subSplit.setVisibility(View.VISIBLE);
        }else {
            transaction.add(R.id.settings_main_attach_point,settingsFragment);
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

