package com.ambientgallery.viewables;

import static com.ambientgallery.utils.AnimateUtil.viewOpacity;
import static com.ambientgallery.utils.BitmapUtil.decodeSampledBitmap;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.DimensUtil.getPartialImageScale;
import static com.ambientgallery.utils.SharedPrefsUtil.ANIMATION_DURATION_SHORT;
import static com.ambientgallery.utils.SharedPrefsUtil.BG_AMBIENT_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.BG_NORMAL_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.IMAGE_QUALITY_LEVEL;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.TEXT_MAIN_AMBIENT_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.TEXT_MAIN_NORMAL_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.viewables.MainActivity.MAIN_IMAGE_PATH;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsBrightnessCardFragment extends Fragment {
    ImageView imageView;
    View parentCard;
    View topShader, bottomShader, mainText;
    SharedPreferences prefs;
    String path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        getParentFragmentManager().setFragmentResultListener("displayMode", this, (requestKey, bundle) -> {
            switch (bundle.getInt("tabNumber")) {
                case 0://normal
                    setDisplayMode(false);
                    break;
                case 1://ambient
                    setDisplayMode(true);
                    break;
                default:
                    break;
            }
        });
        getParentFragmentManager().setFragmentResultListener("viewOpacity", this, (requestKey, bundle) -> {
            switch (bundle.getInt("currentTab") + bundle.getInt("currentView")) {
                case 0://bg
                    imageView.setAlpha(bundle.getFloat("percentage"));
                    break;
                case 1://text
                    mainText.setAlpha(bundle.getFloat("percentage"));
                    break;
                default:
                    break;
            }
        });
        return inflater.inflate(R.layout.fragment_settings_brightness_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = requireActivity().findViewById(R.id.settings_brightness_card_image);
        topShader = requireActivity().findViewById(R.id.settings_brightness_card_top_shader);
        bottomShader = requireActivity().findViewById(R.id.settings_brightness_card_bottom_shader);
        mainText = requireActivity().findViewById(R.id.settings_brightness_card_text_main);
        imageView.setAlpha(prefsFloat(prefs, BG_NORMAL_OPACITY));
        path = requireActivity().getIntent().getStringExtra(MAIN_IMAGE_PATH);
        parentCard=requireActivity().findViewById(R.id.settings_detail_card);
        new Thread(() -> {
            Bitmap bitmap = decodeSampledBitmap(path, getDisplayMetrics(requireActivity().getWindowManager()).width, getDisplayMetrics(requireActivity().getWindowManager()).height, prefsInt(prefs, IMAGE_QUALITY_LEVEL));
            if (bitmap != null) requireActivity().runOnUiThread(() -> {
                imageView.setImageBitmap(bitmap);
                float scale = getPartialImageScale(getDisplayMetrics(requireActivity().getWindowManager()),bitmap.getWidth(),bitmap.getHeight(),parentCard.getWidth(),parentCard.getHeight());
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            });
        }).start();

    }


    protected void setDisplayMode(boolean ambient) {
        float imageOpacity, textOpacity, shaderOpacity;
        if (ambient) {//ambient
            imageOpacity = prefsFloat(prefs, BG_AMBIENT_OPACITY);
            textOpacity = prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY);
            shaderOpacity = 0f;

        } else {//normal
            imageOpacity = prefsFloat(prefs, BG_NORMAL_OPACITY);
            textOpacity = prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY);
            shaderOpacity = 1f;
        }
        viewOpacity(imageView, imageOpacity, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
        viewOpacity(mainText, textOpacity, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
        viewOpacity(topShader, shaderOpacity, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
        viewOpacity(bottomShader, shaderOpacity, 1, 1, prefsInt(prefs, ANIMATION_DURATION_SHORT));
    }
}
