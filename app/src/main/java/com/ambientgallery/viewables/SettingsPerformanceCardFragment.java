package com.ambientgallery.viewables;

import static com.ambientgallery.utils.BitmapUtil.decodeSampledBitmap;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.DimensUtil.getHalfScreenScale;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

public class SettingsPerformanceCardFragment extends Fragment {
    SharedPreferences prefs;
    ImageView imageView;
    String path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null){
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
        }
        path = getActivity().getIntent().getStringExtra("currentPath");
        loadImage();
    }

    private void loadImage(){
       if (getActivity()!=null){
           new Thread(() -> {
               Bitmap bitmap = decodeSampledBitmap(path, getDisplayMetrics(getActivity().getWindowManager()).width, getDisplayMetrics(getActivity().getWindowManager()).height, prefsInt(prefs, "imageQualityLevel"));
               if (bitmap != null) getActivity().runOnUiThread(() -> {
                   Toast.makeText(getContext(), bitmap.getHeight()+"x"+bitmap.getWidth(), Toast.LENGTH_SHORT).show();
                   imageView.setImageBitmap(bitmap);
                   float scale = getHalfScreenScale(getDisplayMetrics(getActivity().getWindowManager()).width, getDisplayMetrics(getActivity().getWindowManager()).height, bitmap.getWidth(), bitmap.getHeight());
                   imageView.setScaleX(scale);
                   imageView.setScaleY(scale);
               });
           }).start();
       }
    }
}
