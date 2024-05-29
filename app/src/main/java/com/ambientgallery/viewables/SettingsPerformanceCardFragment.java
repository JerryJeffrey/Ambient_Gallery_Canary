package com.ambientgallery.viewables;

import static com.ambientgallery.utils.BitmapUtil.decodeSampledBitmap;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.DimensUtil.getHalfScreenScale;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;
import com.ambientgallery.components.DisplayDimensions;

public class SettingsPerformanceCardFragment extends Fragment {
    SharedPreferences prefs;
    ImageView imageView;
    TextView textInfo;
    String path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
        getParentFragmentManager().setFragmentResultListener("imageQualityLevel", this, (requestKey, bundle) -> loadImage());

        return inflater.inflate(R.layout.fragment_settings_performance_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = requireActivity().findViewById(R.id.settings_performance_card_image);
        textInfo = requireActivity().findViewById(R.id.settings_performance_card_info);

        path = requireActivity().getIntent().getStringExtra("currentPath");
        loadImage();
    }

    private void loadImage() {

        new Thread(() -> {
            Bitmap bitmap = decodeSampledBitmap(path, getDisplayMetrics(requireActivity().getWindowManager()).width, getDisplayMetrics(requireActivity().getWindowManager()).height, prefsInt(prefs, "imageQualityLevel"));
            if (bitmap != null) requireActivity().runOnUiThread(() -> {
                setInfo(bitmap.getWidth(), bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
                float scale = getHalfScreenScale(getDisplayMetrics(requireActivity().getWindowManager()).width, getDisplayMetrics(requireActivity().getWindowManager()).height, bitmap.getWidth(), bitmap.getHeight());
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            });
        }).start();

    }

    @SuppressLint("SetTextI18n")
    private void setInfo(int imageWidth, int imageHeight) {
        DisplayDimensions dimensions = getDisplayMetrics(requireActivity().getWindowManager());
        String displayInfo = dimensions.width + " × " + dimensions.height, bitmapInfo = imageWidth + " × " + imageHeight;
        textInfo.setText(getString(R.string.settings_performance_display_resolution) + ": " + displayInfo + "\n" + getString(R.string.settings_performance_image_resolution) + ": " + bitmapInfo);
    }
}
