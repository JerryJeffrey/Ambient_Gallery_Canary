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
        if (getActivity() != null) {
            prefs = getActivity().getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
            getParentFragmentManager().setFragmentResultListener("imageQualityLevel", this, (requestKey, bundle) -> {
                loadImage();
            });
        }

        return inflater.inflate(R.layout.fragment_settings_performance_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            imageView = getActivity().findViewById(R.id.settings_performance_card_image);
            textInfo=getActivity().findViewById(R.id.settings_performance_card_info);
        }
        path = getActivity().getIntent().getStringExtra("currentPath");
        loadImage();
    }

    private void loadImage() {
        if (getActivity() != null) {
            new Thread(() -> {
                Bitmap bitmap = decodeSampledBitmap(path, getDisplayMetrics(getActivity().getWindowManager()).width, getDisplayMetrics(getActivity().getWindowManager()).height, prefsInt(prefs, "imageQualityLevel"));
                if (bitmap != null) getActivity().runOnUiThread(() -> {
                    setInfo(bitmap.getWidth(),bitmap.getHeight());
                    imageView.setImageBitmap(bitmap);
                    float scale = getHalfScreenScale(getDisplayMetrics(getActivity().getWindowManager()).width, getDisplayMetrics(getActivity().getWindowManager()).height, bitmap.getWidth(), bitmap.getHeight());
                    imageView.setScaleX(scale);
                    imageView.setScaleY(scale);
                });
            }).start();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setInfo(int imageWidth, int imageHeight) {
        if (getActivity() != null) {
            DisplayDimensions dimensions = getDisplayMetrics(getActivity().getWindowManager());
            String displayInfo = dimensions.width + " × " + dimensions.height,
                    bitmapInfo = imageWidth + " × " + imageHeight;
            textInfo.setText(getString(R.string.settings_performance_display_resolution)+": "+displayInfo+"\n"+getString(R.string.settings_performance_image_resolution)+": "+bitmapInfo);

        }
    }
}
