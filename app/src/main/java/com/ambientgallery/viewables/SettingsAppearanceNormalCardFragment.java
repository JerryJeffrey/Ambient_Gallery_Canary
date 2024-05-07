package com.ambientgallery.viewables;

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

import static com.ambientgallery.utils.BitmapUtil.decodeSampledBitmap;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

public class SettingsAppearanceNormalCardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate
                (R.layout.fragment_settings_appearance_normal_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences
                    ("MainPrefs", Context.MODE_PRIVATE);
            ImageView imageView = getActivity().
                    findViewById(R.id.settings_appearance_normal_card_image);
            imageView.setAlpha(prefsFloat(prefs, "bgNormalOpacity"));
            String path = getActivity().getIntent().getStringExtra("currentPath");
            view.post(() -> {
                Bitmap bitmap = decodeSampledBitmap(path, imageView.getWidth(),
                        imageView.getHeight(), prefsInt(prefs, "inSampleLevel"));
                getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            });
        }
    }

}
